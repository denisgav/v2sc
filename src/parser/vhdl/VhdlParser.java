package parser.vhdl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import common.MyDebug;

import parser.CommentBlock;
import parser.IASTNode;
import parser.IParser;
import parser.ISymbol;
import parser.ISymbolTable;
import parser.ParserException;
import parser.Token;

public class VhdlParser implements IParser, VhdlTokenConstants, VhdlASTConstants, IVhdlType
{
    protected VhdlTokenManager tokenMgr = null;
    protected VhdlArrayList<SymbolTable> extSymbolTable = new VhdlArrayList<SymbolTable>();
    protected SymbolTable curSymbolTable = null;
    protected ASTNode curNode = null;       // current parsing node
    protected ASTNode lastNode = null;      // last parsed node
    protected ASTNode designFile = null;  // design file node
    protected LibraryManager libMgr = LibraryManager.getInstance();
    
    protected ArrayList<ASTNode> localUnits = null;   // entities/packages in this file
    
    /**
     *  true -- just only parse symbols in package(if exist)<br>
     *  false -- parse all AST
     */
    protected boolean parseSymbol = false;
    
    /**
     * constructor, file path version
     */
    public VhdlParser(boolean parseSymbol) {
        this.parseSymbol = parseSymbol;
    }
    
    /**
     * token which has its own symbol table call this function to start<br>
     * use pair with endBlock
     */
    void startBlock() {
        SymbolTable newTab = new LocalSymbolTable(curSymbolTable, curNode.getName());
        curSymbolTable = newTab;
    }
    
    /**
     * token which has its own symbol table call this function to end<br>
     * use pair with startBlock
     */
    void endBlock() {
        curSymbolTable = curSymbolTable.getParent();
    }
    
    void openNodeScope(ASTNode n) throws ParserException  {
        curNode = n;
        n.setFirstToken(tokenMgr.getNextToken());
        curNode.setSymbolTable(curSymbolTable);
    }
    
    void closeNodeScope(ASTNode n) throws ParserException  {
        n.setLastToken(tokenMgr.getCurrentToken());
        
        lastNode = curNode;
        curNode = (ASTNode)n.getParent();
    }
    
    Token consumeToken(int kind) throws ParserException {
        Token oldToken = tokenMgr.getCurrentToken();
        Token token = tokenMgr.toNextToken();
        if(kind == SEMICOLON) {     // consume continuous semicolons
            if(token == null || token.kind != SEMICOLON) {
                throw new ParserException(oldToken);
            }
            Token prev = token;
            while(token != null && token.kind == SEMICOLON) {
                prev = token;
                token = tokenMgr.toNextToken();
            }
            tokenMgr.setCurrentToken(prev);
            return prev;
        }else {
            if(token != null && token.kind == kind) {
                return token;
            }
        }
        throw new ParserException(oldToken);
    }
    
    /**
     * check whether specified token is behind base token
     */
    boolean checkLateComming(Token token, Token base) throws ParserException {
        if(base == null) { return false; }
        if(token == null) { return true; }
        if(token.beginLine > base.beginLine 
            || (token.beginLine == base.beginLine
                && token.beginColumn > base.beginColumn)) {
            return true;
        }else {
            return false;
        }
    }
    
    /**
     * find token in block between "from" and "to" (including "from", but not "to")
     * before call this function, you must in one block(after keyword token)
     */
    Token findTokenInBlock(Token from, int kind, Token to) throws ParserException {
        Token token = from;
        Token ret = null;
        
        while(token != null)
        {
            if(checkLateComming(token, to) || token == to) {
                break;
            }
            if(token != from && from.beginLine > token.beginLine) {
                break;
            }
            if(token.kind == kind) {
                ret = token;
                break;
            }
            
            Token nextToken = tokenMgr.getNextToken(token);
            if(nextToken == null) {
                break;
            }
            
            Token tmp, tmp1 = null, tmp2 = null;
            int tmpKind = -1;
            switch(token.kind)
            {
            case LBRACKET:
                tmp1 = findTokenInBlock(nextToken, RBRACKET, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case ARCHITECTURE:
            //case ENTITY:
            //case PACKAGE:
                tmp1 = findTokenInBlock(nextToken, END, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case PROCEDURE:
            case FUNCTION:
                tmp2 = findTokenInBlock(nextToken, SEMICOLON, to);
                if(tmp2 == null) { return null; }
                tmp1 = findTokenInBlock(nextToken, IS, tmp2);
                if((tmp1 != null && tmp2 != null && checkLateComming(tmp2, tmp1))
                        || (tmp1 != null && tmp2 == null)) {
                    tmp1 = findTokenInBlock(nextToken, END, to);    // is subprogram body
                    nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                }else {
                    nextToken = tmp2; // is subprogram declaration
                }
                break;
                
            case WAIT:
                nextToken = tokenMgr.getNextToken(token);
                if(nextToken.kind == FOR) {
                    nextToken = tokenMgr.getNextToken(nextToken);
                }
                break;
                
            case FOR:
                if(tokenMgr.getNextTokenKind(nextToken) == IN) {  // generate or loop
                    tmp = nextToken;
                    while(tmp != null && tmp.kind != SEMICOLON)
                    {
                        if(tmp.kind == LOOP || tmp.kind == GENERATE) {
                            tmpKind = tmp.kind;
                            break;
                        }
                        tmp = tokenMgr.getNextToken(tmp);
                    }
                    if(tmp == null || tmp.kind == SEMICOLON) {
                        return null;
                    }
                    tmp = tokenMgr.getNextToken(tmp);
                }else {
                    tmpKind = FOR;
                    tmp = nextToken;
                }
                tmp = findTokenInBlock(tmp, END, to);
                tmp1 = findTokenInBlock(tmp, tmpKind, to);
                if(tmp != null && tmp1 != null && tmp.next == tmp1) {
                    nextToken = tokenMgr.getNextToken(tmp1);  // ignore block
                }
                break;
                
            case WHILE:
                tmp = findTokenInBlock(nextToken, LOOP, to);
                if(tmp == null) {
                    return null;
                }
                tmp = tokenMgr.getNextToken(tmp);
                tmp = findTokenInBlock(tmp, END, to);
                tmp1 = findTokenInBlock(tmp, LOOP, to);
                if(tmp != null && tmp1 != null && tmp.next == tmp1) {
                    nextToken = tokenMgr.getNextToken(tmp1);  // ignore block
                }
                break;
                
            case CASE:
            case COMPONENT:
            case BLOCK:
            case GENERATE:
            case UNITS:
            case RECORD:
            case PROCEDURAL:
            case CONFIGURATION:
            case LOOP:
                tmp = findTokenInBlock(nextToken, END, to);
                tmp1 = findTokenInBlock(tmp, token.kind, to);
                if(tmp != null && tmp1 != null && tmp.next == tmp1) {
                    nextToken = tokenMgr.getNextToken(tmp1);  // ignore block
                }
                break;
                
            case PROCESS:
                tmp = findTokenInBlock(nextToken, END, to);
                tmp1 = findTokenInBlock(tmp, PROCESS, to);
                if((tmp != null && tmp1 != null) 
                    && (tmp.next == tmp1 
                        || (tmp.next != null && tmp.next.kind == POSTPONED 
                                && tmp.next.next == tmp1))) {
                    nextToken = tokenMgr.getNextToken(tmp1);  // ignore block
                }
                break;
                
            case IF:
                tmp = nextToken;
                while(tmp != null && tmp.kind != SEMICOLON)
                {
                    if(tmp.kind == USE || tmp.kind == GENERATE) {
                        tmpKind = tmp.kind;
                        break;
                    }else if(tmp.kind == THEN) {
                        tmpKind = IF;
                        break;
                    }
                    tmp = tokenMgr.getNextToken(tmp);
                }
                if(tmp == null || tmp.kind == SEMICOLON) {
                    return null;
                }
                tmp = tokenMgr.getNextToken(tmp);
                
                tmp = findTokenInBlock(tmp, END, to);
                tmp1 = findTokenInBlock(tmp, tmpKind, to);
                if(tmp1 != null && tmp != null && tmp.next == tmp1) {
                    nextToken = tokenMgr.getNextToken(tmp1);  // ignore block
                }
                break;
                
            case ATTRIBUTE:
                tmp1 = findToken(nextToken, SEMICOLON, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
            default:
                break;
            }
            token = nextToken;
        }
        
        return ret;
    }
    
    /**
     * find token in block between current token and "to" (not including and "to")
     * <br> ignore blocks in this bolck
     * <br>before call this function, you must in one block(after keyword token)
     */
    Token findTokenInBlock(int kind, Token to) throws ParserException {
        return findTokenInBlock(tokenMgr.getNextToken(), kind, to);
    }
    
    /**
     * find token in block between "from" token and "to" (including "from", but not "to")
     * no ignore
     */
    Token findToken(Token from, int kind, Token to) throws ParserException {
        Token token = from;
        Token ret = null;
        while(token != null)
        {
            if(checkLateComming(token, to) || token == to) {
                break;
            }
            else if(token.kind == kind) {
                ret = token;
                break;
            }
            
            token = tokenMgr.getNextToken(token);
        }
        return ret;
    }
    
    /**
     * find token in block between current token and "to" (not including and "to")
     * no ignore
     */
    Token findToken(int kind, Token to) throws ParserException {
        return findToken(tokenMgr.getNextToken(), kind, to);
    }
    
    /**
     *  find last left-bracket token in block between current token and "to" (not including and "to")
     */
    Token findLastLBracketToken(Token to) throws ParserException {
        Token token = findToken(tokenMgr.getNextToken(), LBRACKET, to);
        Token ret = null;
        while(token != null) {
            ret = token;
            token = findTokenInBlock(tokenMgr.getNextToken(token), RBRACKET, to);
            if(token == null) {
                throw new ParserException(to);
            }
            token = findToken(tokenMgr.getNextToken(token), LBRACKET, to);
        }
        return ret;
    }
    
    /**
     *  find last token(except left-bracket) in block between current token and "to" (not including and "to")
     */
    Token findLastTokenInBlock(int kind, Token to) throws ParserException {
        Token ret = null;
        Token token = findTokenInBlock(tokenMgr.getNextToken(), kind, to);
        while(token != null) {
            ret = token;
            token = findTokenInBlock(tokenMgr.getNextToken(token), kind, to);
        }
        return ret;
    }

    boolean tokensExist(int[] tokens, Token endToken) throws ParserException {
        for(int i = 0; i < tokens.length; i++) {
            if(findTokenInBlock(tokens[i], endToken) != null) {
                return true;
            }
        }
        return false;
    }
    
    Token getNearestToken(int[] tokens, Token endToken) throws ParserException {
        Token ret = null;
        Token tmp = null;
        for(int i = 0; i < tokens.length; i++) {
            if((tmp = findTokenInBlock(tokens[i], endToken)) != null) {
                if(ret == null || checkLateComming(ret, tmp)) {
                    ret = tmp;
                }
            }
        }
        return ret;
    }
    
    /** check next is aggregate choice */
    boolean isChoice(Token endToken) throws ParserException {
        return (findTokenInBlock(RARROW, endToken) != null);
    }
    
    
    /** Simple_expression different from other choice */
    boolean isSimple_expression(Token endToken) throws ParserException {
        final int[] tokens = {ADD, SUB, MUL, DIV, MOD, REM, ABS, NOT, EXP, CONCAT};
        return tokensExist(tokens, endToken);
    }
    
    /** discrete_range different from other choice */
    boolean isDiscrete_range(Token endToken) throws ParserException {
        final int[] tokens = {RANGE, DOWNTO, TO, SQUOTE};
        return tokensExist(tokens, endToken);
    }
    
    /** difference relation from common name */
    boolean isQualify_expression(Token endToken) throws ParserException {
        Token token1 = findLastTokenInBlock(SQUOTE, endToken);
        Token token2 = findLastLBracketToken(endToken);
        if(token1 != null && token2 != null && token2 == token1.next) {
            return true;
        }else {
            return false;
        }
    }
    
    /** difference relation from common name */
    boolean isExpression(Token endToken) throws ParserException {
        if(isSimple_expression(endToken) || isQualify_expression(endToken)) {
            return true;
        }
        final int[] tokens = {AND, OR, XOR, NAND, NOR, XNOR, 
                                SLL, SRL, SLA, SRA, ROL, ROR, 
                                GT, GE, EQ, LO, LE, NEQ, NEW, NULL};
        return tokensExist(tokens, endToken);
    }
    
    boolean isFunction_call(ASTNode node, Token endToken) throws ParserException {
        Token token = tokenMgr.getNextToken();
        boolean ret = false;
        if(token == null)
            return ret;

        Symbol sym = (Symbol)getSymbol(node, token.image);  //TODO check selected name
        if((sym != null) && (sym.kind == FUNCTION || sym.kind == PROCEDURE)) {
            Token tmpToken = findLastLBracketToken(endToken);
            if(tokenMgr.getNextToken(token) == tmpToken)
                ret = true;
        }
        return ret;
    }
    
    boolean isType_mark(ASTNode node, Token endToken) throws ParserException {
        Token token = tokenMgr.getNextToken();
        boolean ret = false;
        if(token == null)
            return ret;
        
        // check buildin type
        for(int i = 0; i < strVhdlType.length; i++) {
            if(token.image.equalsIgnoreCase(strVhdlType[i])) {
                return true;
            }
        }

        // user define type
        Symbol sym = (Symbol)getSymbol(node, token.image);  //TODO check selected name
        if((sym != null) && (sym.kind == TYPE || sym.kind == SUBTYPE)) {
            return true;
        }
        
        return ret;
    }
    
    boolean isType_conversion(ASTNode node, Token endToken) throws ParserException {
        Token token = tokenMgr.getNextToken();
        if(token == null)
            return false;
        if(!isType_mark(node, endToken)) {
            return false;
        }
        
        token = tokenMgr.getNextToken(2);
        if(token == null || token.kind != LBRACKET) {
            return false;
        }
        return true;
    }
    
    /**
     *  logical_operator ::=
     *       and | or | nand | nor | xor | xnor
     */
    Token getNextLogical_operator(Token endToken) throws ParserException {
        final int[] tokens = {AND, OR, XOR, NAND, NOR, XNOR};
        return getNearestToken(tokens, endToken);
    }
    
    /**
     *   miscellaneous_operator ::=
     *       ** | abs | not
     */
    Token getNextMiscellaneous_operator(Token endToken) throws ParserException {
        final int[] tokens = {EXP, ABS, NOT};
        return getNearestToken(tokens, endToken);
    }
    
    /**
     * multiplying_operator ::=
     *       * | / | mod | rem
     */
    Token getNextMultiplying_operator(Token endToken) throws ParserException {
        final int[] tokens = {MUL, DIV, MOD, REM};
        return getNearestToken(tokens, endToken);
    }
    
    /**
     * relational_operator ::=
     *       = | /= | < | <= | > | >=
     */
    Token getNextRelational_operator(Token endToken) throws ParserException {
        final int[] tokens = {EQ, NEQ, LO, LE, GT, GE};
        return getNearestToken(tokens, endToken);
    }
    
    /**
     *  shift_operator ::=
     *      sll | srl | sla | sra | rol | ror
     */
    Token getNextShift_operator(Token endToken) throws ParserException {
        final int[] tokens = {SLL, SRL, SLA, SRA, ROL, ROR};
        return getNearestToken(tokens, endToken);
    }
    
    /**
     * adding_operator ::=
     *      + | - | &
     */   
    Token getNextAdding_operator(Token endToken) throws ParserException {
        final int[] tokens = {ADD, SUB, CONCAT};
        return getNearestToken(tokens, endToken);
    }
        
    
    
    /**
     * <dl> abstract_literal ::=
     *   <dd> decimal_literal | based_literal
     */
    void abstract_literal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTABSTRACT_LITERAL);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(!(kind == decimal_literal || kind == based_literal)) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        Token token = tokenMgr.toNextToken();
        new ASTtoken(node, token.image);
        closeNodeScope(node);
    }

    /**
     * <dl> access_type_definition ::=
     *   <dd> <b>access</b> subtype_indication
     */
    void access_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTACCESS_TYPE_DEFINITION);
        openNodeScope(node);
        consumeToken(ACCESS);
        subtype_indication(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> across_aspect ::=
     *   <dd> identifier_list
     *   [ tolerance_aspect ]
     *   [ := expression ] <b>across</b>
     */
    void across_aspect(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTACROSS_ASPECT);
        openNodeScope(node);
        endToken = findToken(ACROSS, endToken);
        identifier_list(node, endToken);
        if(tokenMgr.getNextTokenKind() == TOLERANCE) {
            tolerance_aspect(node, endToken);
        }
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            new ASTtoken(node, tokenImage[ASSIGN]);
            expression(node, endToken);
        }
        consumeToken(ACROSS);
        closeNodeScope(node);
    }

    /**
     * <dl> actual_designator ::=
     *   <dd> expression
     *   <br> | <i>signal_</i>name
     *   <br> | <i>variable_</i>name
     *   <br> | <i>file_</i>name
     *   <br> | <i>terminal_</i>name
     *   <br> | <i>quantity_</i>name
     *   <br> | <b>open</b>
     */
    void actual_designator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTACTUAL_DESIGNATOR);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier) {
            if(isExpression(endToken) || isFunction_call(node, endToken)) {
                expression(node, endToken);
            }else {
                name(node, endToken);
            }
        }else if(kind == OPEN) {
            consumeToken(OPEN);
            new ASTtoken(node, tokenImage[OPEN]);
        }else {
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> actual_parameter_part ::=
     *   <dd> <i>parameter_</i>association_list
     */
    void actual_parameter_part(IASTNode p, Token endToken) throws ParserException {
        association_list(p, endToken);
    }

    /**
     * <dl> actual_part ::=
     *   <dd> actual_designator
     *   <br> | <i>function_</i>name ( actual_designator )
     *   <br> | type_mark ( actual_designator )
     */
    void actual_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTACTUAL_PART);
        openNodeScope(node);
        
        Token tmpToken = findTokenInBlock(LBRACKET, endToken);
        if(tmpToken != null) {
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), 
                                RBRACKET, endToken);
        }
        
        if(tmpToken != null && tmpToken != endToken.prev) {
            actual_designator(node, endToken);
        }else if(isFunction_call(node, endToken)) {
            function_call(node, endToken);
        }else if(isType_mark(node, endToken)) {
            tmpToken = findTokenInBlock(LBRACKET, endToken);
            type_mark(node, tmpToken);
            consumeToken(LBRACKET);
            actual_designator(node, endToken.prev);
            consumeToken(RBRACKET);
        }else {
            actual_designator(node, endToken);
        }
        
        closeNodeScope(node);
    }

    /**
     * <dl> adding_operator ::=
     *   <dd> + | - | &
     */
    void adding_operator(IASTNode p, Token endToken) throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ADD || kind == SUB || kind == CONCAT) {
            consumeToken(kind);
            new ASTtoken(p, tokenImage[kind]);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
    }

    /**
     * <dl> aggregate ::=
     *   <dd> ( element_association { , element_association } )
     */
    void aggregate(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTAGGREGATE);
        openNodeScope(node);
        consumeToken(LBRACKET);
        Token rbracketToken = findTokenInBlock(RBRACKET, endToken);
        if(rbracketToken == null) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        
        while(true) {
            Token commaToken = findTokenInBlock(COMMA, rbracketToken);
            endToken = (commaToken != null) ? commaToken : rbracketToken;
            element_association(node, endToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RBRACKET);
        closeNodeScope(node);
    }

    /**
     * <dl> alias_declaration ::=
     *   <dd> <b>alias</b> alias_designator [ : alias_indication ] <b>is</b> name [ signature ] ;
     */
    void alias_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTALIAS_DECLARATION);
        openNodeScope(node);
        consumeToken(ALIAS);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(IS, endToken);
        alias_designator(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == COLON) {
            consumeToken(COLON);
            new ASTtoken(node, tokenImage[COLON]);
            alias_indication(node, tmpToken);
        }
        consumeToken(IS);
        new ASTtoken(node, tokenImage[IS]);
        
        name(node, endToken);
        if(tokenMgr.getNextTokenKind() != SEMICOLON) {
            signature(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, ALIAS, this);
    }

    /**
     * <dl> alias_designator ::=
     *   <dd> identifier
     *   <br> | character_literal
     *   <br> | operator_symbol
     */
    void alias_designator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTALIAS_DESIGNATOR);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier) {
            identifier(node, endToken);
        }else if(kind == character_literal) {
            Token token = tokenMgr.toNextToken();
            new ASTtoken(node, token.image);
        }else if(kind == string_literal) {
            operator_symbol(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> alias_indication ::=
     *   <dd> subtype_indication
     *   <br> | subnature_indication
     */
    void alias_indication(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTALIAS_INDICATION);
        openNodeScope(node);
        if(findToken(ACROSS, endToken) != null 
            || findToken(THROUGH, endToken) != null) {
            subnature_indication(node, endToken);
        }else {
            subtype_indication(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> allocator ::=
     *   <dd> <b>new</b> subtype_indication
     *   <br> | <b>new</b> qualified_expression
     */
    void allocator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTALLOCATOR);
        openNodeScope(node);
        consumeToken(NEW);
        if(findToken(SQUOTE, endToken) != null) {
            qualified_expression(node, endToken);
        }else if(tokenMgr.getNextTokenKind() == identifier) {
            subtype_indication(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> architecture_body ::=
     *   <dd> <b>architecture</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
     *   <ul> architecture_declarative_part </ul>
     *   <b>begin</b>
     *   <ul> architecture_statement_part </ul>
     *   <b>end</b> [ <b>architecture</b> ] [ <i>architecture_</i>simple_name ] ;
     */
    void architecture_body(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTARCHITECTURE_BODY);
        openNodeScope(node);
        consumeToken(ARCHITECTURE);
        endToken = findTokenInBlock(END, endToken);
        
        Token tmpToken = findToken(OF, endToken);
        identifier(node, tmpToken);        
        consumeToken(OF);
        
        // save old symbol table
        SymbolTable oldTab = curSymbolTable;
        
        startBlock();
        ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);

        tmpToken = findToken(IS, endToken);
        name(node, tmpToken);
        consumeToken(IS);
        
        String name = ((ASTNode)node.getChild(1)).firstTokenImage();
        for(int i = 0; i < localUnits.size(); i++) {
            String tmpName = ((ASTNode)localUnits.get(i)).getName();
            if(name.equalsIgnoreCase(tmpName)) {
                SymbolTable table = ((ASTNode)localUnits.get(i).getChild(0)).symTab;
                node.setSymbolTable(table);
                curSymbolTable.setParent(table);   // set parent as entity
                break;
            }
        }
        
        tmpToken = findTokenInBlock(BEGIN, endToken);
        architecture_declarative_part(node, tmpToken);
        consumeToken(BEGIN);
        
        architecture_statement_part(node, endToken);
        consumeToken(END);
        
        if(tokenMgr.getNextTokenKind() == ARCHITECTURE) {
            consumeToken(ARCHITECTURE);
        }
        
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, ARCHITECTURE, this);
        
        // restore symbol table
        curSymbolTable = oldTab;
    }

    /**
     * <dl> architecture_declarative_part ::=
     *   <dd> { block_declarative_item }
     */
    void architecture_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTARCHITECTURE_DECLARATIVE_PART);
        openNodeScope(node);
        block_declarative_items(node, endToken);
        closeNodeScope(node);
    }
    
    /**
     * <dl> architecture_statement ::=
     *   <dd> simultaneous_statement
     *   <br> | concurrent_statement
     */
    void architecture_statements(IASTNode p, Token endToken) throws ParserException {
        while(true) {
            if(!simultaneous_statement(p, endToken) 
                    && !concurrent_statement(p, endToken)) {
                break;
            }
        }
    }

    /**
     * <dl> architecture_statement_part ::=
     *   <dd> { architecture_statement }
     */
    void architecture_statement_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTARCHITECTURE_STATEMENT_PART);
        openNodeScope(node);
        architecture_statements(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> array_nature_definition ::=
     *   <dd> unconstrained_nature_definition | constrained_nature_definition
     */
    void array_nature_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTARRAY_NATURE_DEFINITION);
        openNodeScope(node);
        if(findToken(INFINITE, endToken) != null) {
            unconstrained_nature_definition(node, endToken);
        }else {
            constrained_nature_definition(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> array_type_definition ::=
     *   <dd> unconstrained_array_definition | constrained_array_definition
     */
    void array_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTARRAY_TYPE_DEFINITION);
        openNodeScope(node);
        if(findToken(INFINITE, endToken) != null) {
            unconstrained_array_definition(node, endToken);
        }else {
            constrained_array_definition(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> assertion ::=
     *   <dd> <b>assert</b> condition
     *   <ul> [ <b>report</b> expression ]
     *   <br> [ <b>severity</b> expression ] </ul>
     */
    void assertion(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTASSERTION);
        openNodeScope(node);
        consumeToken(ASSERT);
        new ASTtoken(node, tokenImage[ASSERT]);
        
        Token reportToken = findToken(REPORT, endToken);
        Token severityToken = findToken(SEVERITY, endToken);
        Token tmpToken = endToken;
        if(reportToken != null)
            tmpToken = reportToken;
        else if(severityToken != null)
            tmpToken = severityToken;
        condition(node, tmpToken);
        
        if(tokenMgr.getNextTokenKind() == REPORT) {
            consumeToken(REPORT);
            new ASTtoken(node, tokenImage[REPORT]);
            if(severityToken != null)
                tmpToken = severityToken;
            expression(node, tmpToken);
        }

        if(tokenMgr.getNextTokenKind() == SEVERITY) {
            consumeToken(SEVERITY);
            new ASTtoken(node, tokenImage[SEVERITY]);
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> assertion_statement ::=
     *   <dd> [ label : ] assertion ;
     */
    void assertion_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTASSERTION_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        
        assertion(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> association_element ::=
     *   <dd> [ formal_part => ] actual_part
     */
    void association_element(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTASSOCIATION_ELEMENT);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(RARROW, endToken);
        if(tmpToken != null) {
            formal_part(node, tmpToken);
            consumeToken(RARROW);
            new ASTtoken(node, tokenImage[RARROW]);
        }
        actual_part(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> association_list ::=
     *   <dd> association_element { , association_element }
     */
    void association_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTASSOCIATION_LIST);
        openNodeScope(node);
        while(true) {
            Token commaToken = findTokenInBlock(COMMA, endToken);
            association_element(node, (commaToken != null) ? commaToken : endToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> attribute_declaration ::=
     *   <dd> <b>attribute</b> identifier : type_mark ;
     */
    void attribute_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTATTRIBUTE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        consumeToken(ATTRIBUTE);
        identifier(node, endToken);
        consumeToken(COLON);
        
        type_mark(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, ATTRIBUTE, this);
    }

    /**
     * <dl> attribute_designator ::=
     *   <dd> <i>attribute_</i>simple_name
     */
    void attribute_designator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTATTRIBUTE_DESIGNATOR);
        openNodeScope(node);
        simple_name(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> attribute_name ::=
     *   <dd> prefix [ signature ] ' attribute_designator [ ( expression { , expression } ) ]
     */
    void attribute_name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTATTRIBUTE_NAME);
        openNodeScope(node);
        Token tk = null;
        if(tokenMgr.getNextToken(2).image.equalsIgnoreCase("range")) {
            tk = tokenMgr.getNextToken(3);
            prefix(node, tk);
            attribute_designator(node, endToken);
            closeNodeScope(node);
            return;
        }
        
        tk = findToken(SQUOTE, endToken);
        prefix(node, tk);
        if(tokenMgr.getNextTokenKind() != SQUOTE) {
            signature(node, tk);
        }
        consumeToken(SQUOTE);
        attribute_designator(node, endToken);
        if(tokenMgr.getNextTokenKind() == LBRACKET) {
            consumeToken(LBRACKET);
            endToken = findTokenInBlock(RBRACKET, endToken);
            while(true) {
                Token commaToken = findTokenInBlock(COMMA, endToken);
                expression(node, (commaToken != null) ? commaToken : endToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
            consumeToken(RBRACKET);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> attribute_specification ::=
     *   <dd> <b>attribute</b> attribute_designator <b>of</b> entity_specification <b>is</b> expression ;
     */
    void attribute_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTATTRIBUTE_SPECIFICATION);
        openNodeScope(node);
        consumeToken(ATTRIBUTE);
        endToken = findToken(SEMICOLON, endToken);
        Token tmpToken = findToken(OF, endToken);
        attribute_designator(node, tmpToken);
        consumeToken(OF);
        
        tmpToken = findToken(IS, endToken);
        entity_specification(node, tmpToken);
        consumeToken(IS);
        
        expression(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> base ::=
     *   <dd> integer
     */
    //void base(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> base_specifier ::=
     *   <dd> b | o | x
     */
    //void base_specifier(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> based_integer ::=
     *   <dd> extended_digit { [underline ] extended_digit }
     */
    //void based_integer(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> based_literal ::=
     *   <dd> base # based_integer [ . based_integer ] # [ exponent ]
     */
    //void based_literal(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> basic_character ::=
     *   <dd>basic_graphic_character | format_effector
     */
    //void basic_character(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> basic_graphic_character ::=
     *   <dd> upper_case_letter | digit | special_character | space_character
     */
    //void basic_graphic_character(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> basic_identifier ::=
     *   <dd> letter { [ underline ] letter_or_digit }
     */
    //void basic_identifier(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> binding_indication ::=
     *   <dd> [ <b>use</b> entity_aspect ]
     *   <br> [ generic_map_aspect ]
     *   <br> [ port_map_aspect ]
     */
    void binding_indication(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBINDING_INDICATION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == USE) {
            consumeToken(USE);
            new ASTtoken(node, tokenImage[USE]);
            entity_aspect(node, endToken);
        }
        if(tokenMgr.getNextTokenKind() == GENERIC) {
            generic_map_aspect(node, endToken);
        }
        if(tokenMgr.getNextTokenKind() == PORT) {
            port_map_aspect(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> bit_string_literal ::=
     *   <dd> base_specifier " [ bit_value ] "
     */
    //void bit_string_literal(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> bit_value ::=
     *   <dd> extended_digit { [ underline ] extended_digit }
     */
    //void bit_value(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> block_configuration ::=
     *   <dd> <b>for</b> block_specification
     *   <ul> { use_clause }
     *   <br> { configuration_item }
     *   </ul> <b>end</b> <b>for</b> ;
     */
    void block_configuration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_CONFIGURATION);
        openNodeScope(node);
        consumeToken(FOR);
        endToken = findTokenInBlock(END, endToken);
        
        Token useToken = findToken(USE, endToken);
        Token configToken = findToken(FOR, endToken);
        Token tmpToken = endToken;
        if(useToken != null)    //TODO to be modify(may check use or config which is the first
            tmpToken = useToken;
        else if(configToken != null)
            tmpToken = configToken;
            
        block_specification(node, tmpToken);
        while(true) {
            if(tokenMgr.getNextTokenKind() == USE) {
                new ASTtoken(node, tokenImage[USE]);
                tmpToken = findToken(SEMICOLON, endToken);
                use_clause(node, tmpToken);
            }else if(tokenMgr.getNextTokenKind() == FOR) {
                new ASTtoken(node, tokenImage[FOR]);
                configuration_item(node, endToken);
            }else {
                break;
            }
        }
        consumeToken(END);
        consumeToken(FOR);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> block_declarative_item ::=
     *   <dd> subprogram_declaration
     *   <br> | subprogram_body
     *   <br> | type_declaration
     *   <br> | subtype_declaration
     *   <br> | constant_declaration
     *   <br> | signal_declaration
     *   <br> | <i>shared_</i>variable_declaration
     *   <br> | file_declaration
     *   <br> | alias_declaration
     *   <br> | component_declaration
     *   <br> | attribute_declaration
     *   <br> | attribute_specification
     *   <br> | configuration_specification
     *   <br> | disconnection_specification
     *   <br> | step_limit_specification
     *   <br> | use_clause
     *   <br> | group_template_declaration
     *   <br> | group_declaration
     *   <br> | nature_declaration
     *   <br> | subnature_declaration
     *   <br> | quantity_declaration
     *   <br> | terminal_declaration
     */
    void block_declarative_items(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        Token tmpToken = null, tmpToken1 = null;
        while(!exitLoop) {
            int kind = tokenMgr.getNextTokenKind();
            switch(kind)
            {
            case PROCEDURE:
            case FUNCTION:
            case IMPURE:
            case PURE:
                tmpToken = tokenMgr.getNextToken();
                if(kind == IMPURE || kind == PURE) {
                    tmpToken = tokenMgr.getNextToken(tmpToken);
                    if(tmpToken == null) {
                        throw new ParserException(tokenMgr.getNextToken());
                    }
                }
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    subprogram_body(p, endToken);
                }else {
                    subprogram_declaration(p, endToken);
                }
                if(kind == IMPURE || kind == PURE) {
                    kind = FUNCTION;
                }
                break;
            case ATTRIBUTE:
                tmpToken = findToken(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, tmpToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    attribute_specification(p, endToken);
                }else {
                    attribute_declaration(p, endToken);
                }
                break;
            case GROUP:
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    group_template_declaration(p, endToken);
                }else {
                    group_declaration(p, endToken);
                }
                break;
            case SHARED:
                kind = VARIABLE;
            case VARIABLE:
                variable_declaration(p, endToken);
                break;
            case TYPE:
                type_declaration(p, endToken);
                break;
            case SUBTYPE:
                subtype_declaration(p, endToken);
                break;
            case CONSTANT:
                constant_declaration(p, endToken);
                break;
            case SIGNAL:
                signal_declaration(p, endToken);
                break;
            case FILE:
                file_declaration(p, endToken);
                break;
            case ALIAS:
                alias_declaration(p, endToken);
                break;
            case DISCONNECT:
                disconnection_specification(p, endToken);
                break;
            case USE:
                use_clause(p, endToken);
                break;
            case COMPONENT:
                component_declaration(p, endToken);
                break;
            case FOR:
                configuration_specification(p, endToken);
                break;
            case NATURE:
                nature_declaration(p, endToken);
                break;
            case SUBNATURE:
                subnature_declaration(p, endToken);
                break;
            case TERMINAL:
                terminal_declaration(p, endToken);
                break;
            case QUANTITY:
                quantity_declaration(p, endToken);
                break;
            case LIMIT:
                step_limit_specification(p, endToken);
                break;
            default:    // not identified
                exitLoop = true;
                break;
            }
        }
    }

    /**
     * <dl> block_declarative_part ::=
     *   <dd> { block_declarative_item }
     */
    void block_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_DECLARATIVE_PART);
        openNodeScope(node);
        block_declarative_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> block_header ::=
     *   <dd> [ generic_clause
     *   <br>   [ generic_map_aspect ; ] ]
     *   <br> [ port_clause
     *   <br>   [ port_map_aspect ; ] ]
     */
    void block_header(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_HEADER);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        Token semicolonToken = findTokenInBlock(SEMICOLON, endToken);
        if(kind == GENERIC) {
            generic_clause(node, tokenMgr.getNextToken(semicolonToken));
            if(tokenMgr.getNextTokenKind() == GENERIC
                    && tokenMgr.getNextTokenKind(2) == MAP) {
                semicolonToken = findTokenInBlock(SEMICOLON, endToken);
                generic_map_aspect(node, semicolonToken);
                consumeToken(SEMICOLON);
            }
        }else if(kind == PORT) {
            port_clause(node, tokenMgr.getNextToken(semicolonToken));
            if(tokenMgr.getNextTokenKind() == PORT
                    && tokenMgr.getNextTokenKind(2) == MAP) {
                semicolonToken = findTokenInBlock(SEMICOLON, endToken);
                port_map_aspect(node, semicolonToken);
                consumeToken(SEMICOLON);
            }
        }
        closeNodeScope(node);
    }

    /**
     * <dl> block_specification ::=
     *   <dd> <i>architecture_</i>name
     *   <br> | <i>block_statement_</i>label
     *   <br> | <i>generate_statement_</i>label [ ( index_specification ) ]
     */
    void block_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_SPECIFICATION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() != identifier) {
            throw new ParserException(tokenMgr.toNextToken()); 
        }
        
        Token tmpToken = findLastLBracketToken(endToken);
        if(tmpToken != null) {
            label(node, tmpToken);
            consumeToken(LBRACKET);
            tmpToken = findTokenInBlock(RBRACKET, endToken);
            index_specification(node, tmpToken);
            consumeToken(RBRACKET);
        }else {
            name(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> block_statement ::=
     *   <dd> <i>block_</i>label :
     *   <ul> <b>block</b> [ ( <i>guard_</i>expression ) ] [ <b>is</b> ]
     *   <ul> block_header
     *   <br> block_declarative_part
     *   </ul> <b>begin</b>
     *   <ul> block_statement_part
     *   </ul> <b>end</b> <b>block</b> [ <i>block_</i>label ] ; </ul>
     */
    void block_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_STATEMENT);
        openNodeScope(node);
        
        Token tmpToken = findToken(COLON, endToken);
        identifier(node, tmpToken);   // label
        consumeToken(COLON);
        consumeToken(BLOCK);
        endToken = findTokenInBlock(END, endToken);
        
        startBlock();
        ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        
        if(tokenMgr.getNextTokenKind() == LBRACKET) {
            consumeToken(LBRACKET);
            tmpToken = findTokenInBlock(RBRACKET, endToken);
            expression(node, tmpToken);
            consumeToken(RBRACKET);
        }
        if(tokenMgr.getNextTokenKind() == IS) {
            consumeToken(IS);
        }
        tmpToken = findTokenInBlock(BEGIN, endToken);
        block_header(node, tmpToken);
        block_declarative_part(node, tmpToken);
        consumeToken(BEGIN);
        block_statement_part(node, endToken);
        consumeToken(END);
        consumeToken(BLOCK);
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, BLOCK, this);
    }

    /**
     * <dl> block_statement_part ::=
     *   <dd> { architecture_statement }
     */
    void block_statement_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_STATEMENT_PART);
        openNodeScope(node);
        architecture_statements(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> branch_quantity_declaration ::=
     *   <dd> <b>quantity</b> [ across_aspect ] [ through_aspect ] terminal_aspect ;
     */
    void branch_quantity_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBRANCH_QUANTITY_DECLARATION);
        openNodeScope(node);
        consumeToken(QUANTITY);
        endToken = findToken(SEMICOLON, endToken);
        if(findToken(ACROSS, endToken) != null) {
            across_aspect(node, endToken);
        }
        if(findToken(THROUGH, endToken) != null) {
            through_aspect(node, endToken);
        }
        terminal_aspect(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> break_element ::=
     *   <dd> [ break_selector_clause ] <i>quantity_</i>name => expression
     */
    void break_element(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBREAK_ELEMENT);
        openNodeScope(node);
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind() == FOR) {
            tmpToken = findToken(USE, endToken);
            break_selector_clause(node, tmpToken);
        }
        tmpToken = findToken(RARROW, endToken);
        name(node, tmpToken);
        consumeToken(RARROW);
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> break_list ::=
     *   <dd> break_element { , break_element }
     */
    void break_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBREAK_LIST);
        openNodeScope(node);
        while(true) {
            Token commaToken = findTokenInBlock(COMMA, endToken);
            break_element(node, (commaToken != null) ? commaToken : endToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> break_selector_clause ::=
     *   <dd> <b>for</b> <i>quantity_</i>name <b>use</b>
     */
    void break_selector_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBREAK_SELECTOR_CLAUSE);
        openNodeScope(node);
        consumeToken(FOR);
        endToken = findToken(USE, endToken);
        name(node, endToken);
        consumeToken(USE);
        closeNodeScope(node);
    }

    /**
     * <dl> break_statement ::=
     *   <dd> [ label : ] <b>break</b> [ break_list ] [ <b>when</b> condition ] ;
     */
    void break_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBREAK_STATEMENT);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            Token tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
        }
        consumeToken(BREAK);
        new ASTtoken(node, tokenImage[BREAK]);
        endToken = findToken(SEMICOLON, endToken);
        
        if(findToken(RARROW, endToken) != null) {
            break_list(node, endToken);
        }
        if(tokenMgr.getNextTokenKind() == WHEN) {
            consumeToken(WHEN);
            new ASTtoken(node, tokenImage[WHEN]);
            condition(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> case_statement ::=
     *   <dd> [ <i>case_</i>label : ]
     *   <ul> <b>case</b> expression <b>is</b>
     *   <ul> case_statement_alternative
     *   <br> { case_statement_alternative }
     *   </ul> <b>end</b> <b>case</b> [ <i>case_</i>label ] ; </ul>
     */
    void case_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCASE_STATEMENT);
        openNodeScope(node);
        
        boolean hasLabel = false;        
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
            hasLabel = true;
        }
        consumeToken(CASE);
        new ASTtoken(node, tokenImage[CASE]);
        endToken = findTokenInBlock(END, endToken);
        
        tmpToken = findToken(IS, endToken);
        expression(node, tmpToken);
        consumeToken(IS);
        tmpToken = findTokenInBlock(tokenMgr.getNextToken(), WHEN, endToken);
        while(true) {
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), WHEN, endToken);
            case_statement_alternative(node, (tmpToken != null) ? tmpToken : endToken);
            if(tokenMgr.getNextTokenKind() != WHEN) {
                break;
            }
        }
        consumeToken(END);
        consumeToken(CASE);
        if(tokenMgr.getNextTokenKind() == identifier) {
            if(!hasLabel) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> case_statement_alternative ::=
     *   <dd> <b>when</b> choices =>
     *   <ul> sequence_of_statements </ul>
     */
    void case_statement_alternative(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCASE_STATEMENT_ALTERNATIVE);
        openNodeScope(node);
        consumeToken(WHEN);
        Token tmpToken = findTokenInBlock(RARROW, endToken);
        choices(node, tmpToken);
        consumeToken(RARROW);
        sequence_of_statements(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> character_literal ::=
     *   <dd> ' graphic_character '
     */
    //void character_literal(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> choice ::=
     *   <dd> simple_expression
     *   <br> | discrete_range
     *   <br> | <i>element_</i>simple_name
     *   <br> | <b>others</b>
     */
    void choice(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCHOICE);
        openNodeScope(node);
        if(isDiscrete_range(endToken)) {
            discrete_range(node, endToken);
        }else if(tokenMgr.getNextTokenKind() == identifier) {
            simple_name(node, endToken);
        }else if(tokenMgr.getNextTokenKind() == OTHERS) {
            consumeToken(OTHERS);
            new ASTtoken(node, tokenImage[OTHERS]);
        }else {
            simple_expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> choices ::=
     *   <dd> choice { | choice }
     */
    void choices(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCHOICES);
        openNodeScope(node);
        while(true) {
            Token pipeToken = findToken(PIPE, endToken);
            choice(node, (pipeToken != null) ? pipeToken : endToken);
            if(tokenMgr.getNextTokenKind() != PIPE) {
                break;
            }
            consumeToken(PIPE);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> component_configuration ::=
     *   <dd> <b>for</b> component_specification
     *   <ul> [ binding_indication ; ]
     *   <br> [ block_configuration ]
     *   </ul> <b>end</b> <b>for</b> ;
     */
    void component_configuration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMPONENT_CONFIGURATION);
        openNodeScope(node);
        
        consumeToken(FOR);
        endToken = findTokenInBlock(END, endToken);
        
        component_specification(node, endToken);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == USE || kind == GENERIC || kind == PORT) {
            Token tmpToken = findToken(SEMICOLON, endToken);
            binding_indication(node, tmpToken);
            consumeToken(SEMICOLON);
        }
        if(tokenMgr.getNextTokenKind() == FOR) {
            block_configuration(node, endToken);
        }
        consumeToken(END);
        consumeToken(FOR);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> component_declaration ::=
     *   <dd> <b>component</b> identifier [ <b>is</b> ]
     *   <ul> [ <i>local_</i>generic_clause ]
     *   <br> [ <i>local_</i>port_clause ]
     *   </ul> <b>end</b> <b>component</b> [ <i>component_</i>simple_name ] ;
     */
    void component_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMPONENT_DECLARATION);
        openNodeScope(node);
        
        consumeToken(COMPONENT);
        endToken = findTokenInBlock(END, endToken);
        
        identifier(node, endToken);
        if(tokenMgr.getNextTokenKind() == IS) {
            consumeToken(IS);
        }
        
        startBlock();
        ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind() == GENERIC) {
            tmpToken = findTokenInBlock(SEMICOLON, endToken);
            generic_clause(node, tokenMgr.getNextToken(tmpToken));
        }
        
        if(tokenMgr.getNextTokenKind() == PORT) {
            tmpToken = findTokenInBlock(SEMICOLON, endToken);
            port_clause(node, tokenMgr.getNextToken(tmpToken));
        }
        consumeToken(END);
        consumeToken(COMPONENT);
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, COMPONENT, this);
    }

    /**
     * <dl> component_instantiation_statement ::=
     *   <dd> <i>instantiation_</i>label :
     *   <ul> instantiated_unit
     *   <ul> [ generic_map_aspect ]
     *   <br> [ port_map_aspect ] ; </ul></ul>
     */
    void component_instantiation_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMPONENT_INSTANTIATION_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        identifier(node, endToken);   // label
        consumeToken(COLON);
        instantiated_unit(node, endToken);
        if(tokenMgr.getNextTokenKind() == GENERIC) {
            generic_map_aspect(node, endToken);
        }
        
        if(tokenMgr.getNextTokenKind() == PORT) {
            port_map_aspect(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> component_specification ::=
     *   <dd> instantiation_list : <i>component_</i>name
     */
    void component_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMPONENT_SPECIFICATION);
        openNodeScope(node);
        instantiation_list(node, endToken);
        consumeToken(COLON);
        endToken = tokenMgr.getNextToken(2);
        name(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> composite_nature_definition ::=
     *   <dd> array_nature_definition
     *   <br> | record_nature_definition
     */
    void composite_nature_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMPOSITE_NATURE_DEFINITION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ARRAY) {
            array_nature_definition(node, endToken);
        }else if(kind == RECORD) {
            record_nature_definition(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> composite_type_definition ::=
     *   <dd> array_type_definition
     *   <br> | record_type_definition
     */
    void composite_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMPOSITE_TYPE_DEFINITION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ARRAY) {
            array_type_definition(node, endToken);
        }else if(kind == RECORD) {
            record_type_definition(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> concurrent_assertion_statement ::=
     *   <dd> [ label : ] [ <b>postponed</b> ] assertion ;
     */
    void concurrent_assertion_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONCURRENT_ASSERTION_STATEMENT);
        openNodeScope(node);

        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        
        if(tokenMgr.getNextTokenKind() == POSTPONED) {
            consumeToken(POSTPONED);
            new ASTtoken(node, tokenImage[POSTPONED]);
        }
        assertion(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> concurrent_break_statement ::=
     *   <dd> [ label : ] <b>break</b> [ break_list ] [ sensitivity_clause ] [ <b>when</b> condition ] ;
     */
    void concurrent_break_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONCURRENT_BREAK_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        consumeToken(BREAK);
        new ASTtoken(node, tokenImage[BREAK]);

        if(findToken(RARROW, endToken) != null) {
            break_list(node, endToken);
        }
        if(tokenMgr.getNextTokenKind() == ON) {
            sensitivity_clause(node, endToken);
        }
        if(tokenMgr.getNextTokenKind() == WHEN) {
            consumeToken(WHEN);
            new ASTtoken(node, tokenImage[WHEN]);
            condition(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> concurrent_procedure_call_statement ::=
     *   <dd> [ label : ] [ <b>postponed</b> ] procedure_call ;
     */
    void concurrent_procedure_call_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONCURRENT_PROCEDURE_CALL_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        
        if(tokenMgr.getNextTokenKind() == POSTPONED) {
            consumeToken(POSTPONED);
            new ASTtoken(node, tokenImage[POSTPONED]);
        }
        procedure_call(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> concurrent_signal_assignment_statement ::=
     *   <dd> [ label : ] [ <b>postponed</b> ] conditional_signal_assignment
     *   <br> | [ label : ] [ <b>postponed</b> ] selected_signal_assignment
     */
    void concurrent_signal_assignment_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONCURRENT_SIGNAL_ASSIGNMENT_STATEMENT);
        openNodeScope(node);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        
        if(tokenMgr.getNextTokenKind() == POSTPONED) {
            consumeToken(POSTPONED);
            new ASTtoken(node, tokenImage[POSTPONED]);
        }
        if(tokenMgr.getNextTokenKind() == WITH) {
            selected_signal_assignment(node, endToken);
        }else {
            conditional_signal_assignment(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> concurrent_statement ::=
     *   <dd> block_statement
     *   <br> | process_statement
     *   <br> | concurrent_procedure_call_statement
     *   <br> | concurrent_assertion_statement
     *   <br> | concurrent_signal_assignment_statement
     *   <br> | component_instantiation_statement
     *   <br> | generate_statement
     *   <br> | concurrent_break_statement
     *   @return 
     *     true: this node has been handled, false elsewhere
     */
    boolean concurrent_statement(IASTNode p, Token endToken) throws ParserException {
        Token tmpToken = tokenMgr.getNextToken();
        if(tmpToken == null) { return false; }
        if(tmpToken.kind == SEMICOLON || tmpToken.kind == BEGIN
                || tmpToken.kind == END) {
            return false;
        }
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = tokenMgr.getNextToken(tmpToken);   // ignore label
            tmpToken = tokenMgr.getNextToken(tmpToken);
            if(tmpToken == null) { return false; }
        }
        
        if(tmpToken.kind == POSTPONED) {
            tmpToken = tokenMgr.getNextToken(tmpToken);   // ignore postponed
            if(tmpToken == null) { return false; }
        }
        
        ASTNode node = null;
        boolean isSignal_assignment = false;
        boolean isGeneric_or_port_map = false;
        if(!(tmpToken.kind == BLOCK || tmpToken.kind == PROCESS
                || tmpToken.kind == ASSERT 
                || tmpToken.kind == COMPONENT || tmpToken.kind == ENTITY
                || tmpToken.kind == CONFIGURATION || tmpToken.kind == FOR
                || tmpToken.kind == IF || tmpToken.kind == BREAK)) {
            Token tmpToken1 = findToken(tmpToken, SEMICOLON, endToken);
            if(tmpToken1 != null) {
                if(findToken(tmpToken, LE, tmpToken1) != null) {
                    isSignal_assignment = true;
                }else if(findToken(tmpToken, GENERIC, tmpToken1) != null 
                        || findToken(tmpToken, PORT, tmpToken1) != null) {
                    isGeneric_or_port_map = true;
                }
            }
            
            if(!isSignal_assignment && !isGeneric_or_port_map 
                    && tmpToken.kind != identifier) {
                return false;
            }
        }

        node = new ASTNode(p, ASTCONCURRENT_STATEMENT);
        openNodeScope(node);
        
        switch(tmpToken.kind)
        {
        case BLOCK:
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), END, endToken);
            if(tmpToken == null) { return false; }
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            block_statement(node, tmpToken);
            break;
        case PROCESS:
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), END, endToken);
            if(tmpToken == null) { return false; }
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            process_statement(node, tmpToken);
            break;
        case ASSERT:
            tmpToken = findTokenInBlock(tmpToken, SEMICOLON, endToken);
            concurrent_assertion_statement(node, tokenMgr.getNextToken(tmpToken));
            break;
        case COMPONENT:
        case ENTITY:
        case CONFIGURATION:
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            component_instantiation_statement(node, tokenMgr.getNextToken(tmpToken));
            break;
        case FOR:
        case IF:
            tmpToken = findToken(tmpToken, GENERATE, endToken);
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), END, endToken);
            generate_statement(node, tmpToken);
            break;
        case BREAK:
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            concurrent_break_statement(node, tokenMgr.getNextToken(tmpToken));
            break;
            
        default:
            if(isSignal_assignment) {
                tmpToken = findToken(tmpToken, SEMICOLON, endToken);
                concurrent_signal_assignment_statement(node, tokenMgr.getNextToken(tmpToken));
            }else if(isGeneric_or_port_map) {
                tmpToken = findToken(tmpToken, SEMICOLON, endToken);
                component_instantiation_statement(node, tokenMgr.getNextToken(tmpToken));
            }else if(tmpToken.kind == identifier) {
                concurrent_procedure_call_statement(node, endToken);
            }else{
                return false;
            }
            break;
        }
        
        closeNodeScope(node);
        return true;
    }

    /**
     * <dl> condition ::=
     *   <dd> <i>boolean_</i>expression
     */
    void condition(IASTNode p, Token endToken) throws ParserException {
        if(tokenMgr.getNextToken().kind == LBRACKET) {
            Token tk1 = findTokenInBlock(tokenMgr.getNextToken(2), RBRACKET, endToken);
            if(tokenMgr.getNextToken(tk1) == endToken) {
                tokenMgr.toNextToken();
                expression(p, endToken.prev);
                tokenMgr.toNextToken();
            }else {
                expression(p, endToken);
            }
        }else {
            expression(p, endToken);
        }
    }

    /**
     * <dl> condition_clause ::=
     *   <dd> <b>until</b> condition
     */
    void condition_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONDITION_CLAUSE);
        openNodeScope(node);
        consumeToken(UNTIL);
        new ASTtoken(node, tokenImage[UNTIL]);
        condition(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> conditional_signal_assignment ::=
     *   <dd> target <= options conditional_waveforms ;
     */
    void conditional_signal_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONDITIONAL_SIGNAL_ASSIGNMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(LE, endToken);
        target(node, tmpToken);
        consumeToken(LE);
        new ASTtoken(node, tokenImage[LE]);
        options(node, endToken);
        conditional_waveforms(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> conditional_waveforms ::=
     *   <dd> { waveform <b>when</b> condition <b>else</b> }
     *   <br> waveform [ <b>when</b> condition ]
     */
    void conditional_waveforms(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONDITIONAL_WAVEFORMS);
        openNodeScope(node);
        Token tmpToken1, tmpToken2;
        while(true) {
            tmpToken1 = findToken(ELSE, endToken);
            if(tmpToken1 == null) {
                break;
            }
            tmpToken2 = findToken(WHEN, tmpToken1);
            waveform(node, tmpToken2);
            consumeToken(WHEN);
            new ASTtoken(node, tokenImage[WHEN]);
            condition(node, tmpToken1);
            consumeToken(ELSE);
        }

        tmpToken1 = findToken(WHEN, endToken);
        waveform(node, (tmpToken1 != null) ? tmpToken1 : endToken);
        if(tokenMgr.getNextTokenKind() == WHEN) {
            consumeToken(WHEN);
            new ASTtoken(node, tokenImage[WHEN]);
            condition(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> configuration_declaration ::=
     *   <dd> <b>configuration</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
     *   <ul> configuration_declarative_part
     *   <br> block_configuration
     *   </ul><b>end</b> [ <b>configuration</b> ] [ <i>configuration_</i>simple_name ] ;
     */
    void configuration_declaration(IASTNode p, Token endToken) throws ParserException {
        
        if(parseSymbol) {
            // if only parse symbol, just ignore it
            // because we need only package declaration
            endToken = findTokenInBlock(tokenMgr.getNextToken(2), END, endToken);
            Token semToken = findToken(endToken, SEMICOLON, null);
            if(semToken == null) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            tokenMgr.setCurrentToken(semToken);
            return;
        }
        
        ASTNode node = new ASTNode(p, ASTCONFIGURATION_DECLARATION);
        openNodeScope(node);
        
        consumeToken(CONFIGURATION);
        endToken = findTokenInBlock(END, endToken);
        
        Token tmpToken = findToken(OF, endToken);
        identifier(node, tmpToken);
        consumeToken(OF);
        new ASTtoken(node, tokenImage[OF]);
        
        tmpToken = findToken(IS, endToken);
        name(node, tmpToken);
        consumeToken(IS);
        new ASTtoken(node, tokenImage[IS]);
        
        tmpToken = findToken(FOR, endToken);
        configuration_declarative_part(node, tmpToken);
        
        block_configuration(node, endToken);
        consumeToken(END);
        if(tokenMgr.getNextTokenKind() == CONFIGURATION) {
            consumeToken(CONFIGURATION);
        }
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> configuration_declarative_item ::=
     *   <dd> use_clause
     *   <br> | attribute_specification
     *   <br> | group_declaration
     */
    void configuration_declarative_items(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        while(!exitLoop) {
            int kind = tokenMgr.getNextTokenKind();
            if(kind == USE) {
                use_clause(p, endToken);
            }else if(kind == ATTRIBUTE) {
                attribute_specification(p, endToken);
            }else if(kind == GROUP) {
                group_declaration(p, endToken);
            }else {
                exitLoop = true;
            }            
        }
    }

    /**
     * <dl> configuration_declarative_part ::=
     *   <dd> { configuration_declarative_item }
     */
    void configuration_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONFIGURATION_DECLARATIVE_PART);
        openNodeScope(node);
        configuration_declarative_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> configuration_item ::=
     *   <dd> block_configuration
     *   <br> | component_configuration
     */
    void configuration_item(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONFIGURATION_ITEM);
        openNodeScope(node);
        if(findToken(COLON, endToken) != null) { //TODO modify here
            component_configuration(node, endToken);
        }else {
            block_configuration(node, endToken);
        }
        openNodeScope(node);
    }

    /**
     * <dl> configuration_specification ::=
     *   <dd> <b>for</b> component_specification binding_indication ;
     */
    void configuration_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONFIGURATION_SPECIFICATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(FOR);
        component_specification(node, endToken);
        binding_indication(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> constant_declaration ::=
     *   <dd> <b>constant</b> identifier_list : subtype_indication [ := expression ] ;
     */
    void constant_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONSTANT_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(COLON, endToken);
        consumeToken(CONSTANT);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        
        tmpToken = findToken(ASSIGN, endToken);
        subtype_indication(node, (tmpToken != null) ? tmpToken : endToken);
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            expression(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, CONSTANT, this);
    }

    /**
     * <dl> constrained_array_definition ::=
     *   <dd> <b>array</b> index_constraint <b>of</b> <i>element_</i>subtype_indication
     */
    void constrained_array_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONSTRAINED_ARRAY_DEFINITION);
        openNodeScope(node);
        Token tmpToken = findToken(OF, endToken);
        consumeToken(ARRAY);
        index_constraint(node, tmpToken);
        consumeToken(OF);
        subtype_indication(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> constrained_nature_definition ::=
     *   <dd> <b>array</b> index_constraint <b>of</b> subnature_indication
     */
    void constrained_nature_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONSTRAINED_NATURE_DEFINITION);
        openNodeScope(node);
        Token tmpToken = findToken(OF, endToken);
        consumeToken(ARRAY);
        index_constraint(node, tmpToken);
        consumeToken(OF);
        subnature_indication(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> constraint ::=
     *   <dd> range_constraint
     *   <br> | index_constraint
     */
    void constraint(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONSTRAINT);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == RANGE) {
            range_constraint(node, endToken);
        }else {
            index_constraint(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> context_clause ::=
     *   <dd> { context_item }
     */
    void context_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONTEXT_CLAUSE);
        openNodeScope(node);
        context_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> context_item ::=
     *   <dd> library_clause
     *   <br> | use_clause
     */
    void context_items(IASTNode p, Token endToken) throws ParserException {
        while(true) {
            int kind = tokenMgr.getNextTokenKind();
            if(kind == LIBRARY) {
                library_clause(p, endToken);
            }else if(kind == USE) {
                use_clause(p, endToken);
            }else {
                break;
            }
        }
    }

    /**
     * <dl> decimal_literal ::=
     *   <dd> integer [ . integer ] [ exponent ]
     */
    //void decimal_literal(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> declaration ::=
     *   <dd> type_declaration
     *   <br> | subtype_declaration
     *   <br> | constant_declaration 
     *   <br> | signal_declaration 
     *   <br> | variable_declaration 
     *   <br> | file_declaration 
     *   <br> | interface_declaration ;
     *   <br> | alias_declaration
     *   <br> | attribute_declaration
     *   <br> | component_declaration
     *   <br> | group_template_declaration
     *   <br> | group_declaration
     *   <br> | entity_declaration
     *   <br> | configuration_declaration
     *   <br> | subprogram_declaration
     *   <br> | package_declaration
     *   <br> | nature_declaration
     *   <br> | subnature_declaration
     *   <br> | quantity_declaration
     *   <br> | terminal_declaration
     */
    void declarations(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        Token tmpToken, tmpToken1;
        while(!exitLoop) {
            if(findToken(IN, endToken) != null
                    || findToken(OUT, endToken) != null
                    || findToken(INOUT, endToken) != null
                    || findToken(BUFFER, endToken) != null
                    || findToken(LINKAGE, endToken) != null) {
                tmpToken = findToken(SEMICOLON, endToken);
                interface_declaration(p, tmpToken);
                consumeToken(SEMICOLON);
                continue;
            }
            
            int kind = tokenMgr.getNextTokenKind();
            switch(kind)
            {
            case PROCEDURE:
            case FUNCTION:
            case IMPURE:
            case PURE:
                subprogram_declaration(p, endToken);
                break;
            case ATTRIBUTE:
                attribute_declaration(p, endToken);
                break;
            case GROUP:
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    group_template_declaration(p, endToken);
                }else {
                    group_declaration(p, endToken);
                }
                break;
            case SHARED:
            case VARIABLE:
                variable_declaration(p, endToken);
                break;
            case TYPE:
                type_declaration(p, endToken);
                break;
            case SUBTYPE:
                subtype_declaration(p, endToken);
                break;
            case CONSTANT:
                constant_declaration(p, endToken);
                break;
            case SIGNAL:
                signal_declaration(p, endToken);
                break;
            case FILE:
                file_declaration(p, endToken);
                break;
            case ALIAS:
                alias_declaration(p, endToken);
                break;
            case COMPONENT:
                component_declaration(p, endToken);
                break;
            case NATURE:
                nature_declaration(p, endToken);
                break;
            case SUBNATURE:
                subnature_declaration(p, endToken);
                break;
            case TERMINAL:
                terminal_declaration(p, endToken);
                break;
            case QUANTITY:
                quantity_declaration(p, endToken);
                break;
            case ENTITY:
                entity_declaration(p, endToken);
                break;
            case PACKAGE:
                package_declaration(p, endToken);
                break;
            case CONFIGURATION:
                configuration_declaration(p, endToken);
                break;
            case identifier:
                tmpToken = findToken(SEMICOLON, endToken);
                interface_declaration(p, tmpToken);
                consumeToken(SEMICOLON);
                break;
            default:    // not identified
                exitLoop = true;
                break;
            }
        }
    }

    /**
     * <dl> delay_mechanism ::=
     *   <dd> <b>transport</b>
     *   <br> | [ <b>reject</b> <i>time_</i>expression ] <b>inertial</b>
     */
    void delay_mechanism(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY_MECHANISM);
        openNodeScope(node);
        Token tmpToken = findToken(INERTIAL, endToken);
        if(tokenMgr.getNextTokenKind() == TRANSPORT) {
            consumeToken(TRANSPORT);
            new ASTtoken(node, tokenImage[TRANSPORT]);
        }else if(tmpToken != null) {
            if(tokenMgr.getNextTokenKind() == REJECT) {
                consumeToken(REJECT);
                expression(node, tmpToken);
            }
            consumeToken(INERTIAL);
            new ASTtoken(node, tokenImage[INERTIAL]);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> design_file ::=
     *   <dd> design_unit { design_unit }
     */
    ASTNode design_file() throws ParserException {
        ASTNode node = new ASTNode(null, ASTDESIGN_FILE);
        openNodeScope(node);
        localUnits = new ArrayList<ASTNode>();
        while(true) {
            design_unit(node, null);
            int kind = tokenMgr.getNextTokenKind();
            if(!(kind == ARCHITECTURE || kind == CONFIGURATION
                || kind == ENTITY || kind == LIBRARY
                || kind == PACKAGE || kind == USE)) {
                break;
            }
        }
        closeNodeScope(node);
        
        designFile = node;
        return node;
    }

    /**
     * <dl> design_unit ::=
     *   <dd> context_clause library_unit
     */
    void design_unit(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDESIGN_UNIT);
        openNodeScope(node);
        context_clause(node, endToken);
        library_unit(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> designator ::=
     *   <dd> identifier | operator_symbol
     */
    void designator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDESIGNATOR);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier) {
            identifier(node, endToken);
        }else if(kind == string_literal) {
            operator_symbol(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> direction ::=
     *   <dd> <b>to</b> | <b>downto</b>
     */
    void direction(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDIRECTION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == TO || kind == DOWNTO) {
            consumeToken(kind);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> disconnection_specification ::=
     *   <dd> <b>disconnect</b> guarded_signal_specification <b>after</b> <i>time_</i>expression ;
     */
    void disconnection_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDISCONNECTION_SPECIFICATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(AFTER, endToken);
        consumeToken(DISCONNECT);
        guarded_signal_specification(node, tmpToken);
        consumeToken(AFTER);
        expression(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> discrete_range ::=
     *   <dd> <i>discrete_</i>subtype_indication | range
     */
    void discrete_range(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDISCRETE_RANGE);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == RANGE
                || isDiscrete_range(endToken)) {
            range(node, endToken);
        }else if(tokenMgr.getNextTokenKind() == identifier) {
            subtype_indication(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> element_association ::=
     *   <dd> [ choices => ] expression
     */
    void element_association(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTELEMENT_ASSOCIATION);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(RARROW, endToken);
        if(tmpToken != null) {
            choices(node, tmpToken);
            consumeToken(RARROW);
            new ASTtoken(node, tokenImage[RARROW]);
        }
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> element_declaration ::=
     *   <dd> identifier_list : element_subtype_definition ;
     */
    void element_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTELEMENT_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        element_subtype_definition(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, RECORD, this);
    }

    /**
     * <dl> element_subnature_definition ::=
     *   <dd> subnature_indication
     */
    void element_subnature_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTELEMENT_SUBNATURE_DEFINITION);
        openNodeScope(node);
        subnature_indication(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> element_subtype_definition ::=
     *   <dd> subtype_indication
     */
    void element_subtype_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTELEMENT_SUBTYPE_DEFINITION);
        openNodeScope(node);
        subtype_indication(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> entity_aspect ::=
     *   <dd> <b>entity</b> <i>entity_</i>name [ ( <i>architecture_</i>identifier ) ]
     *   <br> | <b>configuration</b> <i>configuration_</i>name
     *   <br> | <b>open</b>
     */
    void entity_aspect(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_ASPECT);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        consumeToken(kind);
        if(kind == ENTITY) {
            name(node, endToken);
            if(tokenMgr.getNextTokenKind() == LBRACKET) {
                consumeToken(LBRACKET);
                identifier(node, endToken.prev);
                consumeToken(RBRACKET);
            }
            new ASTtoken(node, tokenImage[ENTITY]);
        }else if(kind == CONFIGURATION) {
            name(node, endToken);
            new ASTtoken(node, tokenImage[CONFIGURATION]);
        }else if(kind == OPEN) {
            new ASTtoken(node, tokenImage[OPEN]);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> entity_class ::=
     *   <dd> <b>entity</b>
     *   <br> | <b>architecture</b>
     *   <br> | <b>configuration</b>
     *   <br> | <b>procedure</b>
     *   <br> | <b>function</b>
     *   <br> | <b>package</b>
     *   <br> | <b>type</b>
     *   <br> | <b>subtype</b>
     *   <br> | <b>constant</b>
     *   <br> | <b>signal</b>
     *   <br> | <b>variable</b>
     *   <br> | <b>component</b>
     *   <br> | <b>label</b>
     *   <br> | <b>literal</b>
     *   <br> | <b>units</b>
     *   <br> | <b>group</b>
     *   <br> | <b>file</b>
     *   <br> | <b>nature</b>
     *   <br> | <b>subnature</b>
     *   <br> | <b>quantity</b>
     *   <br> | <b>terminal</b>
     */
    void entity_class(IASTNode p, Token endToken) throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(!(kind == ENTITY || kind == ARCHITECTURE || kind == CONFIGURATION
                || kind == PROCEDURE || kind == FUNCTION
                || kind == PACKAGE || kind == TYPE
                || kind == SUBTYPE || kind == CONSTANT
                || kind == SIGNAL || kind == VARIABLE
                || kind == COMPONENT || kind == LABEL
                || kind == LITERAL || kind == UNITS
                || kind == GROUP || kind == FILE
                || kind == SUBNATURE || kind == NATURE
                || kind == TERMINAL)) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        consumeToken(kind);
        new ASTtoken(p, tokenImage[kind]);
    }

    /**
     * <dl> entity_class_entry ::=
     *   <dd> entity_class [ <> ]
     */
    void entity_class_entry(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_CLASS_ENTRY);
        openNodeScope(node);
        entity_class(node, endToken);
        if(tokenMgr.getNextTokenKind() == INFINITE) {
            consumeToken(INFINITE);
            new ASTtoken(node, tokenImage[INFINITE]);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> entity_class_entry_list ::=
     *   <dd> entity_class_entry { , entity_class_entry }
     */
    void entity_class_entry_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_CLASS_ENTRY_LIST);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            entity_class_entry(node, (tmpToken != null) ? tmpToken : endToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> entity_declaration ::=
     *   <dd> <b>entity</b> identifier <b>is</b>
     *   <ul> entity_header
     *   <br> entity_declarative_part
     *   </ul> [ <b>begin</b>
     *   <ul> entity_statement_part ]
     *   </ul> <b>end</b> [ <b>entity</b> ] [ <i>entity_</i>simple_name ] ;
     */
    void entity_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_DECLARATION);
        openNodeScope(node);
        
        consumeToken(ENTITY);
        endToken = findTokenInBlock(END, endToken);

        Token tmpToken = findToken(IS, endToken);
        identifier(node, tmpToken);

        consumeToken(IS);
        
        startBlock();
        ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        tmpToken = findTokenInBlock(BEGIN, endToken);
        
        if(tmpToken == null)
            tmpToken = endToken;
        entity_header(node, tmpToken);
        entity_declarative_part(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == BEGIN) {
            consumeToken(BEGIN);
            entity_statement_part(node, endToken);
        }
        consumeToken(END);
        if(tokenMgr.getNextTokenKind() == ENTITY) {
            consumeToken(ENTITY);
        }
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        localUnits.add(node);
        closeNodeScope(node);
    }

    /**
     * <dl> entity_declarative_item ::=
     *   <dd> subprogram_declaration
     *   <br> | subprogram_body
     *   <br> | type_declaration
     *   <br> | subtype_declaration
     *   <br> | constant_declaration
     *   <br> | signal_declaration
     *   <br> | <i>shared_</i>variable_declaration
     *   <br> | file_declaration
     *   <br> | alias_declaration
     *   <br> | attribute_declaration
     *   <br> | attribute_specification
     *   <br> | disconnection_specification
     *   <br> | step_limit_specification
     *   <br> | use_clause
     *   <br> | group_template_declaration
     *   <br> | group_declaration
     *   <br> | nature_declaration
     *   <br> | subnature_declaration
     *   <br> | quantity_declaration
     *   <br> | terminal_declaration
     */
    void entity_declarative_items(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        Token tmpToken = null, tmpToken1 = null;
        while(!exitLoop) {
            int kind = tokenMgr.getNextTokenKind();
            switch(kind)
            {
            case PROCEDURE:
            case FUNCTION:
            case IMPURE:
            case PURE:
                tmpToken = tokenMgr.getNextToken();
                if(kind == IMPURE || kind == PURE) {
                    tmpToken = tokenMgr.getNextToken(tmpToken);
                    if(tmpToken == null) {
                        throw new ParserException(tokenMgr.getNextToken());
                    }
                }
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    subprogram_body(p, endToken);
                }else {
                    subprogram_declaration(p, endToken);
                }
                if(kind == IMPURE || kind == PURE) {
                    kind = FUNCTION;
                }
                break;
            case ATTRIBUTE:
                tmpToken = findToken(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, tmpToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    attribute_specification(p, endToken);
                }else {
                    attribute_declaration(p, endToken);
                }
                break;
            case GROUP:
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    group_template_declaration(p, endToken);
                }else {
                    group_declaration(p, endToken);
                }
                break;
            case SHARED:
                kind = VARIABLE;
            case VARIABLE:
                variable_declaration(p, endToken);
                break;
            case TYPE:
                type_declaration(p, endToken);
                break;
            case SUBTYPE:
                subtype_declaration(p, endToken);
                break;
            case CONSTANT:
                constant_declaration(p, endToken);
                break;
            case SIGNAL:
                signal_declaration(p, endToken);
                break;
            case FILE:
                file_declaration(p, endToken);
                break;
            case ALIAS:
                alias_declaration(p, endToken);
                break;
            case DISCONNECT:
                disconnection_specification(p, endToken);
                break;
            case USE:
                use_clause(p, endToken);
                break;
            case NATURE:
                nature_declaration(p, endToken);
                break;
            case SUBNATURE:
                subnature_declaration(p, endToken);
                break;
            case TERMINAL:
                terminal_declaration(p, endToken);
                break;
            case QUANTITY:
                quantity_declaration(p, endToken);
                break;
            case LIMIT:
                step_limit_specification(p, endToken);
                break;
            default:    // not identified
                exitLoop = true;
                break;
            }
        }
    }

    /**
     * <dl> entity_declarative_part ::=
     *   <dd> { entity_declarative_item }
     */
    void entity_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_DECLARATIVE_PART);
        openNodeScope(node);
        entity_declarative_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> entity_designator ::=
     *   <dd> entity_tag [ signature ]
     */
    void entity_designator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_DESIGNATOR);
        openNodeScope(node);
        entity_tag(node, endToken);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier || kind == RETURN) {
            signature(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> entity_header ::=
     *   <dd> [ <i>formal_</i>generic_clause ]
     *   <br> [ <i>formal_</i>port_clause ]
     */
    void entity_header(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_HEADER);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == GENERIC) {
            generic_clause(node, endToken);
        }
        if(tokenMgr.getNextTokenKind() == PORT) {
            port_clause(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> entity_name_list ::=
     *   <dd> entity_designator { , entity_designator }
     *   <br> | <b>others</b>
     *   <br> | <b>all</b>
     */
    void entity_name_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_NAME_LIST);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == OTHERS) {
            consumeToken(OTHERS);
            new ASTtoken(node, tokenImage[OTHERS]);
        }else if(kind == ALL) {
            consumeToken(ALL);
            new ASTtoken(node, tokenImage[ALL]);
        }else {
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                entity_designator(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
        }
        closeNodeScope(node);
    }

    /**
     * <dl> entity_specification ::=
     *   <dd> entity_name_list : entity_class
     */
    void entity_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_SPECIFICATION);
        openNodeScope(node);
        Token tmpToken = findToken(COLON, endToken);
        entity_name_list(node, tmpToken);
        consumeToken(COLON);
        entity_class(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> entity_statement ::=
     *   <dd> concurrent_assertion_statement
     *   <br> | <i>passive_</i>concurrent_procedure_call_statement
     *   <br> | <i>passive_</i>process_statement
     */
    void entity_statements(IASTNode p, Token endToken) throws ParserException {
        while(true) {
            if(findToken(ASSERT, endToken) != null) {
                concurrent_assertion_statement(p, endToken);
            }else if(findToken(PROCESS, endToken) != null) {
                process_statement(p, endToken);
            }else if(findToken(identifier, endToken) != null) {
                concurrent_procedure_call_statement(p, endToken);
            }else {
                break;
            }
        }
    }

    /**
     * <dl> entity_statement_part ::=
     *   <dd> { entity_statement }
     */
    void entity_statement_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_STATEMENT_PART);
        openNodeScope(node);
        entity_statements(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> entity_tag ::=
     *   <dd> simple_name
     *   <br> | character_literal
     *   <br> | operator_symbol
     */
    void entity_tag(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENTITY_TAG);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier) {
            simple_name(node, endToken);
        }else if(kind == character_literal) {
            Token token = tokenMgr.toNextToken();
            new ASTtoken(node, token.image);
        }else if(kind == string_literal) {
            operator_symbol(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> enumeration_literal ::=
     *   <dd> identifier | character_literal
     */
    void enumeration_literal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENUMERATION_LITERAL);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier) {
            identifier(node, endToken);
        }else if(kind == character_literal) {
            Token token = tokenMgr.toNextToken();
            new ASTtoken(node, token.image);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
        if(node.getAncestor(ASTENUMERATION_TYPE_DEFINITION) != null) {
            // regard enum as constant integer 
            SymbolParser.parseCommonKind(node, CONSTANT, strVhdlType[TYPE_INTEGER], this);
        }

    }

    /**
     * <dl> enumeration_type_definition ::=
     *   <dd> ( enumeration_literal { , enumeration_literal } )
     */
    void enumeration_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENUMERATION_TYPE_DEFINITION);
        openNodeScope(node);
        consumeToken(LBRACKET);
        endToken = findTokenInBlock(RBRACKET, endToken);
        
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            enumeration_literal(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RBRACKET);
        closeNodeScope(node);
    }

    /**
     * <dl> exit_statement ::=
     *   <dd> [ label : ] <b>exit</b> [ <i>loop_</i>label ] [ <b>when</b> condition ] ;
     */
    void exit_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEXIT_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        
        consumeToken(EXIT);
        new ASTtoken(node, tokenImage[EXIT]);
        if(tokenMgr.getNextTokenKind() == identifier) {
            label(node, endToken);
        }
        if(tokenMgr.getNextTokenKind() == WHEN) {
            consumeToken(WHEN);
            new ASTtoken(node, tokenImage[WHEN]);
            condition(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> exponent ::=
     *   <dd> e [ + ] integer | e - integer
     */
    //void exponent(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> expression ::=
     *   <dd> relation { <b>and</b> relation }
     *   <br> | relation { <b>or</b> relation }
     *   <br> | relation { <b>xor</b> relation }
     *   <br> | relation [ <b>nand</b> relation ]
     *   <br> | relation [ <b>nor</b> relation ]
     *   <br> | relation { <b>xnor</b> relation }
     */
    void expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEXPRESSION);
        
        openNodeScope(node);
        
        //TODO special handle, decrease level of tree
        if(tokenMgr.getNextToken(2) == endToken) {
            // only one token, regard as literal
            literal(node, endToken);
            closeNodeScope(node);
            return;
        }
        
        //TODO expression type: boolean, time, string, guard,
        //                      static, real, value, file_open_kind
        while(true) {
            Token expToken = endToken;
            Token tmpToken = null;
            if((tmpToken = getNextLogical_operator(endToken)) != null) {
                expToken = tmpToken;
            }
            relation(node, expToken);
            int kind = tokenMgr.getNextTokenKind();
            if(!(kind == AND || kind == OR || kind == XOR
                || kind == NAND || kind == NOR || kind == XNOR)) {
                break;
            }
            consumeToken(kind);
            new ASTtoken(node, tokenImage[kind]);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> extended_digit ::=
     *   <dd> digit | letter
     */
    //void extended_digit(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> extended_identifier ::=
     *   <dd> \ graphic_character { graphic_character } \
     */
    //void extended_identifier(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> factor ::=
     *   <dd> primary [ ** primary ]
     *   <br> | <b>abs</b> primary
     *   <br> | <b>not</b> primary
     */
    void factor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFACTOR);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ABS) {
            consumeToken(ABS);
            new ASTtoken(node, tokenImage[ABS]);
            primary(node, endToken);
        }else if(kind == NOT) {
            consumeToken(NOT);
            new ASTtoken(node, tokenImage[NOT]);
            primary(node, endToken);
        }else {
            Token tmpToken = findTokenInBlock(tokenMgr.getNextToken(), EXP, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            primary(node, tmpToken);
            if(tokenMgr.getNextTokenKind() == EXP) {
                consumeToken(EXP);
                new ASTtoken(node, tokenImage[EXP]);
                primary(node, endToken);
            }
        }
        closeNodeScope(node);
    }

    /**
     * <dl> file_declaration ::=
     *   <dd> <b>file</b> identifier_list : subtype_indication [ file_open_information ] ;
     */
    void file_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFILE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(COLON, endToken);
        consumeToken(FILE);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        subtype_indication(node, endToken);
        if(tokenMgr.getNextTokenKind() == OPEN
                || tokenMgr.getNextTokenKind() == IS) {
            file_open_information(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, FILE, this);
    }

    /**
     * <dl> file_logical_name ::=
     *   <dd> <i>string_</i>expression
     */
    void file_logical_name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFILE_LOGICAL_NAME);
        openNodeScope(node);
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> file_open_information ::=
     *   <dd> [ <b>open</b> <i>file_open_kind_</i>expression ] <b>is</b> file_logical_name
     */
    void file_open_information(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFILE_OPEN_INFORMATION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == OPEN) {
            consumeToken(OPEN);
            Token tmpToken = findToken(IS, endToken);
            expression(node, tmpToken);
        }
        consumeToken(IS);
        new ASTtoken(node, tokenImage[IS]);
        file_logical_name(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> file_type_definition ::=
     *   <dd> <b>file of</b> type_mark
     */
    void file_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFILE_TYPE_DEFINITION);
        openNodeScope(node);
        consumeToken(FILE);
        consumeToken(OF);
        type_mark(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> floating_type_definition ::=
     *   <dd> range_constraint
     */
    void floating_type_definition(IASTNode p, Token endToken) throws ParserException {
        range_constraint(p, endToken);
    }

    /**
     * <dl> formal_designator ::=
     *   <dd> <i>generic_</i>name
     *   <br> | <i>port_</i>name
     *   <br> | <i>parameter_</i>name
     */
    void formal_designator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFORMAL_DESIGNATOR);
        openNodeScope(node);
        name(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> formal_parameter_list ::=
     *   <dd> <i>parameter_</i>interface_list
     */
    void formal_parameter_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFORMAL_PARAMETER_LIST);
        openNodeScope(node);
        interface_list(node, endToken);
        closeNodeScope(node);
        SymbolParser.parseInterface_listKind(node, VARIABLE, this);
    }

    /**
     * <dl> formal_part ::=
     *   <dd> formal_designator
     *   <br> | <i>function_</i>name ( formal_designator )
     *   <br> | type_mark  ( formal_designator )
     */
    void formal_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFORMAL_PART);
        openNodeScope(node);
        
        if(isFunction_call(node, endToken)) {
            function_call(node, endToken);
        }else if(isType_mark(node, endToken)) {
            Token tmpToken = findTokenInBlock(LBRACKET, endToken);
            type_mark(node, tmpToken);
            consumeToken(LBRACKET);
            formal_designator(node, endToken.prev);
            consumeToken(RBRACKET);
        }else {
            formal_designator(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> free_quantity_declaration ::=
     *   <dd> <b>quantity</b> identifier_list : subtype_indication [ := expression ] ;
     */
    void free_quantity_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFREE_QUANTITY_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(QUANTITY);
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        
        tmpToken = findToken(ASSIGN, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        subtype_indication(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            new ASTtoken(node, tokenImage[ASSIGN]);
            expression(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> full_type_declaration ::=
     *   <dd> <b>type</b> identifier <b>is</b> type_definition ;
     */
    void full_type_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFULL_TYPE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(IS, endToken);
        consumeToken(TYPE);
        identifier(node, tmpToken);
        consumeToken(IS);
        startBlock();
        ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        
        type_definition(node, endToken);
        endBlock();
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> function_call ::=
     *   <dd> <i>function_</i>name [ ( actual_parameter_part ) ]
     */
    void function_call(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFUNCTION_CALL);
        openNodeScope(node);
        Token tmpToken = findLastLBracketToken(endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        name(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == LBRACKET) {
            consumeToken(LBRACKET);
            tmpToken = findTokenInBlock(RBRACKET, endToken);
            actual_parameter_part(node, tokenMgr.getNextToken(tmpToken));
            consumeToken(RBRACKET);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> generate_statement ::=
     *   <dd> <i>generate_</i>label :
     *   <ul> generation_scheme <b>generate</b>
     *   <ul> [ {block_declarative_item }
     *   </ul> <b>begin</b> ]
     *   <ul> { architecture_statement }
     *   </ul> <b>end</b> <b>generate</b> [ <i>generate_</i>label ] ; </ul>
     */
    void generate_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGENERATE_STATEMENT);
        openNodeScope(node);
        
        Token tmpToken = findToken(COLON, endToken);    // endToken must be END
        identifier(node, tmpToken);   // label
        consumeToken(COLON);
        
        startBlock();
        ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        
        tmpToken = findToken(GENERATE, endToken);
        generation_scheme(node, tmpToken);
        consumeToken(GENERATE);        
        new ASTtoken(node, tokenImage[GENERATE]);
        Token endToken0 = findTokenInBlock(END, endToken);
        
        tmpToken = findTokenInBlock(BEGIN, endToken);
        if(endToken0 == null && tmpToken != null) {
            block_declarative_part(node, tmpToken);
            consumeToken(BEGIN);
            new ASTtoken(node, tokenImage[BEGIN]);
        }
        
        tmpToken = findTokenInBlock(END, endToken);
        architecture_statement_part(node, endToken);
        
        consumeToken(END);
        consumeToken(GENERATE);
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        closeNodeScope(node);
    }

    /**
     * <dl> generation_scheme ::=
     *   <dd> <b>for</b> <i>generate_</i>parameter_specification
     *   <br> | <b>if</b> condition
     */
    void generation_scheme(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGENERATION_SCHEME);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == FOR) {
            consumeToken(FOR);
            new ASTtoken(node, tokenImage[FOR]);
            parameter_specification(node, endToken);
        }else if(kind == IF) {
            consumeToken(IF);
            new ASTtoken(node, tokenImage[IF]);
            condition(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> generic_clause ::=
     *   <dd> <b>generic</b> ( generic_list ) ;
     */
    void generic_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGENERIC_CLAUSE);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        
        consumeToken(GENERIC);
        consumeToken(LBRACKET);
        generic_list(node, endToken.prev);
        consumeToken(RBRACKET);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> generic_list ::=
     *   <dd> <i>generic_</i>interface_list
     */
    void generic_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGENERIC_LIST);
        openNodeScope(node);
        interface_list(node, endToken);
        closeNodeScope(node);
        SymbolParser.parseInterface_listKind(node, GENERIC, this);
    }

    /**
     * <dl> generic_map_aspect ::=
     *   <dd> <b>generic</b> <b>map</b> ( <i>generic_</i>association_list )
     */
    void generic_map_aspect(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGENERIC_MAP_ASPECT);
        openNodeScope(node);
        consumeToken(GENERIC);
        consumeToken(MAP);
        consumeToken(LBRACKET);
        endToken = findTokenInBlock(RBRACKET, endToken);
        association_list(node, endToken);
        consumeToken(RBRACKET);
        closeNodeScope(node);
    }

    /**
     * <dl> graphic_character ::=
     *   <dd> basic_graphic_character | lower_case_letter | other_special_character
     */
    //void graphic_character(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> group_constituent ::=
     *   <dd> name | character_literal
     */
    void group_constituent(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGROUP_CONSTITUENT);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() != character_literal) {
            Token token = tokenMgr.toNextToken();
            new ASTtoken(node, token.image);
        }else {
            name(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> group_constituent_list ::=
     *   <dd> group_constituent { , group_constituent }
     */
    void group_constituent_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGROUP_CONSTITUENT_LIST);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            group_constituent(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> group_declaration ::=
     *   <dd> <b>group</b> identifier : <i>group_template_</i>name ( group_constituent_list ) ;
     */
    void group_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGROUP_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(COLON, endToken);
        consumeToken(GROUP);
        identifier(node, tmpToken);
        consumeToken(COLON);
        
        tmpToken = findLastLBracketToken(endToken);
        name(node, tmpToken);
        consumeToken(LBRACKET);
        tmpToken = findTokenInBlock(RBRACKET, endToken);
        group_constituent_list(node, tmpToken);
        consumeToken(RBRACKET);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, GROUP, this);
    }

    /**
     * <dl> group_template_declaration ::=
     *   <dd> <b>group</b> identifier <b>is</b> ( entity_class_entry_list ) ;
     */
    void group_template_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGROUP_TEMPLATE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(IS, endToken);
        consumeToken(GROUP);
        identifier(node, tmpToken);
        consumeToken(IS);
        consumeToken(LBRACKET);
        tmpToken = findTokenInBlock(RBRACKET, endToken);
        entity_class_entry_list(node, tmpToken);
        consumeToken(RBRACKET);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, GROUP, this);
    }

    /**
     * <dl> guarded_signal_specification ::=
     *   <dd> <i>guarded_</i>signal_list : type_mark
     */
    void guarded_signal_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGUARDED_SIGNAL_SPECIFICATION);
        openNodeScope(node);
        Token tmpToken = findToken(COLON, endToken);
        signal_list(node, tmpToken);
        consumeToken(COLON);
        type_mark(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> identifier ::=
     *   <dd> basic_identifier | extended_identifier
     */
    void identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTIDENTIFIER);
        openNodeScope(node);
        Token token = tokenMgr.toNextToken();
        new ASTtoken(node, token.image);
        closeNodeScope(node);
    }

    /**
     * <dl> identifier_list ::=
     *   <dd> identifier { , identifier }
     */
    void identifier_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTIDENTIFIER_LIST);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            identifier(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> if_statement ::=
     *   <dd> [ <i>if_</i>label : ]
     *   <ul> <b>if</b> condition <b>then</b>
     *   <ul>  sequence_of_statements
     *   </ul> { <b>elsif</b> condition <b>then</b>
     *   <ul> sequence_of_statements }
     *   </ul> [ <b>else</b>
     *   <ul> sequence_of_statements ]
     *   </ul> <b>end</b> <b>if</b> [ <i>if_</i>label ] ; </ul>
     */
    void if_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTIF_STATEMENT);
        openNodeScope(node);
        
        Token tmpToken = findToken(IF, endToken);
        boolean hasLabel = false;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
            hasLabel = true;
        }
        consumeToken(IF);
        new ASTtoken(node, tokenImage[IF]);
        endToken = findTokenInBlock(END, endToken);
        
        tmpToken = findTokenInBlock(THEN, endToken);
        condition(node, tmpToken);
        consumeToken(THEN);
        
        tmpToken = findTokenInBlock(ELSIF, endToken);
        if(tmpToken == null)
            tmpToken = findTokenInBlock(ELSE, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        sequence_of_statements(node, tmpToken);
        
        while(true) {
            if(tokenMgr.getNextTokenKind() != ELSIF) {
                break;
            }
            consumeToken(ELSIF);
            new ASTtoken(node, tokenImage[ELSIF]);
            
            tmpToken = findTokenInBlock(THEN, endToken);
            condition(node, tmpToken);
            consumeToken(THEN);
            
            tmpToken = findTokenInBlock(ELSIF, endToken);
            if(tmpToken == null)
                tmpToken = findTokenInBlock(ELSE, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            sequence_of_statements(node, tmpToken);
        }
        
        if(tokenMgr.getNextTokenKind() == ELSE) {
            consumeToken(ELSE);
            new ASTtoken(node, tokenImage[ELSE]);
            sequence_of_statements(node, endToken);
        }        
        consumeToken(END);
        consumeToken(IF);
        if(tokenMgr.getNextTokenKind() == identifier) {
            if(!hasLabel) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> incomplete_type_declaration ::=
     *   <dd> <b>type</b> identifier ;
     */
    void incomplete_type_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINCOMPLETE_TYPE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(TYPE);
        identifier(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> index_constraint ::=
     *   <dd> ( discrete_range { , discrete_range } )
     */
    void index_constraint(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINDEX_CONSTRAINT);
        openNodeScope(node);
        consumeToken(LBRACKET);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            discrete_range(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RBRACKET);
        closeNodeScope(node);
    }

    /**
     * <dl> index_specification ::=
     *   <dd> discrete_range
     *   <br> | <i>static_</i>expression
     */
    void index_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINDEX_SPECIFICATION);
        openNodeScope(node);
        if(isSimple_expression(endToken) || findToken(RANGE, endToken) != null) {
            discrete_range(node, endToken);
        }else {
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> index_subtype_definition ::=
     *   <dd> type_mark <b>range</b> <>
     */
    void index_subtype_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINDEX_SUBTYPE_DEFINITION);
        openNodeScope(node);
        endToken = findToken(RANGE, endToken);
        
        type_mark(node, endToken);
        consumeToken(RANGE);
        consumeToken(INFINITE);
        closeNodeScope(node);
    }

    /**
     * <dl> indexed_name ::=
     *   <dd> prefix ( expression { , expression } )
     */
    void indexed_name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINDEXED_NAME);
        openNodeScope(node);
        Token tmpToken = findLastLBracketToken(endToken);
        prefix(node, tmpToken);
        consumeToken(LBRACKET);
        
        endToken = findTokenInBlock(RBRACKET, endToken);
        while(true) {
            tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            expression(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RBRACKET);
        closeNodeScope(node);
    }

    /**
     * <dl> instantiated_unit ::=
     *   <dd> [ <b>component</b> ] <i>component_</i>name
     *   <br> | <b>entity</b> <i>entity_</i>name [ ( <i>architecture_</i>identifier ) ]
     *   <br> | <b>configuration</b> <i>configuration_</i>name
     */
    void instantiated_unit(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINSTANTIATED_UNIT);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == COMPONENT || kind == ENTITY 
                || kind == CONFIGURATION) {
            consumeToken(kind);
            new ASTtoken(node, tokenImage[kind]);
        }
        Token tmpToken = findTokenInBlock(LBRACKET, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        name(node, tmpToken);
        if(kind == ENTITY && tokenMgr.getNextTokenKind() == LBRACKET) {
            consumeToken(LBRACKET);
            tmpToken = findTokenInBlock(RBRACKET, endToken);
            identifier(node, tmpToken);
            consumeToken(RBRACKET);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> instantiation_list ::=
     *   <dd> <i>instantiation_</i>label { , <i>instantiation_</i>label }
     *   <br> | <b>others</b>
     *   <br> | <b>all</b>
     */
    void instantiation_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINSTANTIATION_LIST);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier) {
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                label(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
        }else if(kind == OTHERS || kind == ALL) {
            consumeToken(kind);
            new ASTtoken(node, tokenImage[kind]);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> integer ::=
     *   <dd> digit { [ underline ] digit }
     */
    //void integer(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> integer_type_definition ::=
     *   <dd> range_constraint
     */
    void integer_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTEGER_TYPE_DEFINITION);
        openNodeScope(node);
        range_constraint(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> interface_constant_declaration ::=
     *   <dd> [ <b>constant</b> ] identifier_list : [ <b>in</b> ] 
     *          subtype_indication [ := <i>static_</i>expression ]
     */
    void interface_constant_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_CONSTANT_DECLARATION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == CONSTANT) {
            consumeToken(CONSTANT);
            new ASTtoken(node, tokenImage[CONSTANT]);
        }
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        if(tokenMgr.getNextTokenKind() == IN) {
            mode(node, endToken);
        }
        
        tmpToken = findToken(ASSIGN, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        subtype_indication(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            new ASTtoken(node, tokenImage[ASSIGN]);
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> interface_declaration ::=
     *   <dd> interface_constant_declaration
     *   <br> | interface_signal_declaration
     *   <br> | interface_variable_declaration
     *   <br> | interface_file_declaration
     *   <br> | interface_terminal_declaration
     *   <br> | interface_quantity_declaration
     */
    void interface_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_DECLARATION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == SIGNAL || findToken(BUS, endToken) != null) {
            interface_signal_declaration(node, endToken);
        }else if(kind == CONSTANT) {
            interface_constant_declaration(node, endToken);
        }else if(kind == FILE) {
            interface_file_declaration(node, endToken);
        }else if(kind == TERMINAL) {
            interface_terminal_declaration(node, endToken);
        }else if(kind == QUANTITY) {
            interface_quantity_declaration(node, endToken);
        }else {
            interface_variable_declaration(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> interface_element ::=
     *   <dd> interface_declaration
     */
    void interface_element(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_ELEMENT);
        openNodeScope(node);
        interface_declaration(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> interface_file_declaration ::=
     *   <dd> <b>file</b> identifier_list : subtype_indication
     */
    void interface_file_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_FILE_DECLARATION);
        openNodeScope(node);
        Token tmpToken = findToken(COLON, endToken);
        consumeToken(FILE);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        subtype_indication(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> interface_list ::=
     *   <dd> interface_element { ; interface_element }
     */
    void interface_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_LIST);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findToken(SEMICOLON, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            interface_element(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != SEMICOLON) {
                break;
            }
            consumeToken(SEMICOLON);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> interface_quantity_declaration ::=
     *   <dd> <b>quantity</b> identifier_list : [ <b>in</b> | <b>out</b> ] subtype_indication [ := <i>static_</i>expression ]
     */
    void interface_quantity_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_QUANTITY_DECLARATION);
        openNodeScope(node);
        consumeToken(QUANTITY);
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        
        int kind = tokenMgr.getNextTokenKind();
        if(kind == IN || kind == OUT) {
            mode(node, endToken);
        }
        
        tmpToken = findToken(ASSIGN, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        subtype_indication(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            new ASTtoken(node, tokenImage[ASSIGN]);
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> interface_signal_declaration ::=
     *   <dd> [ <b>signal</b> ] identifier_list : [ mode ] 
     *          subtype_indication [ <b>bus</b> ] [ := <i>static_</i>expression ]
     */
    void interface_signal_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_SIGNAL_DECLARATION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == SIGNAL) {
            consumeToken(SIGNAL);
        }
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == IN || kind == OUT || kind == INOUT
                || kind == BUFFER || kind == LINKAGE) {
            mode(node, endToken);
        }
        
        tmpToken = findToken(BUS, endToken);
        if(tmpToken == null)
            tmpToken = findToken(ASSIGN, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        subtype_indication(node, tmpToken);
        
        if(tokenMgr.getNextTokenKind() == BUS) {
            consumeToken(BUS);
            new ASTtoken(node, tokenImage[BUS]);
        }

        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            new ASTtoken(node, tokenImage[ASSIGN]);
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> interface_terminal_declaration ::=
     *   <dd> <b>terminal</b> identifier_list : subnature_indication
     */
    void interface_terminal_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_TERMINAL_DECLARATION);
        openNodeScope(node);
        Token tmpToken = findToken(COLON, endToken);
        consumeToken(TERMINAL);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        subnature_indication(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> interface_variable_declaration ::=
     *   <dd> [ <b>variable</b> ] identifier_list : [ mode ] subtype_indication [ := <i>static_</i>expression ]
     */
    void interface_variable_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTERFACE_VARIABLE_DECLARATION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == VARIABLE) {
            consumeToken(VARIABLE);
        }
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == IN || kind == OUT || kind == INOUT
                || kind == BUFFER || kind == LINKAGE) {
            mode(node, endToken);
        }
        
        tmpToken = findToken(ASSIGN, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        subtype_indication(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            new ASTtoken(node, tokenImage[ASSIGN]);
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> iteration_scheme ::=
     *   <dd> <b>while</b> condition
     *   <br> | <b>for</b> <i>loop_</i>parameter_specification
     */
    void iteration_scheme(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTITERATION_SCHEME);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        consumeToken(kind);
        if(kind == WHILE) {
            new ASTtoken(node, tokenImage[WHILE]);
            condition(node, endToken);
        }else if(kind == FOR) {
            new ASTtoken(node, tokenImage[FOR]);
            parameter_specification(node, endToken);
            // loop variable
            SymbolParser.parseCommonKind(node, LOOP, strVhdlType[TYPE_INTEGER], this);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> label ::=
     *   <dd> identifier
     */
    void label(IASTNode p, Token endToken) throws ParserException {
        identifier(p, endToken);
    }

    /**
     * <dl> letter ::=
     *   <dd> upper_case_letter | lower_case_letter
     */
    //void letter(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> letter_or_digit ::=
     *   <dd> letter | digit
     */
    //void letter_or_digit(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> library_clause ::=
     *   <dd> <b>library</b> logical_name_list ;
     */
    void library_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIBRARY_CLAUSE);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(LIBRARY);
        logical_name_list(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> library_unit ::=
     *   <dd> primary_unit
     *   <br> | secondary_unit
     */
    void library_unit(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIBRARY_UNIT);
        openNodeScope(node);

        int kind = tokenMgr.getNextTokenKind();
        int kind2 = tokenMgr.getNextTokenKind(2);
        if(kind == ARCHITECTURE || (kind == PACKAGE && kind2 == BODY)) {
            secondary_unit(node, endToken);
        }else if(kind == ENTITY || kind == CONFIGURATION || kind == PACKAGE) {
            primary_unit(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> literal ::=
     *   <dd> numeric_literal
     *   <br> | enumeration_literal
     *   <br> | string_literal
     *   <br> | bit_string_literal
     *   <br> | <b>null</b>
     */
    void literal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLITERAL);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier || kind == character_literal) {
            enumeration_literal(node, endToken);
        }else if(kind == decimal_literal || kind == based_literal) {
            numeric_literal(node, endToken);
        }else if(kind == string_literal || kind == bit_string_literal
                || kind == NULL) {
            Token token = tokenMgr.toNextToken();
            ASTNode tkNode = new ASTtoken(node, token.image);
            tkNode.setFirstToken(token);
            tkNode.setLastToken(token);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> logical_name ::=
     *   <dd> identifier
     */
    void logical_name(IASTNode p, Token endToken) throws ParserException {
        identifier(p, endToken);
    }

    /**
     * <dl> logical_name_list ::=
     *   <dd> logical_name { , logical_name }
     */
    void logical_name_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLOGICAL_NAME_LIST);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            logical_name(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> loop_statement ::=
     *   <dd> [ <i>loop_</i>label : ]
     *   <ul> [ iteration_scheme ] <b>loop</b>
     *   <ul> sequence_of_statements
     *   </ul> <b>end</b> <b>loop</b> [ <i>loop_</i>label ] ; </ul>
     */
    void loop_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLOOP_STATEMENT);
        openNodeScope(node);
        Token tmpToken = endToken;
        boolean hasLabel = false;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
            hasLabel = true;
        }
        
        tmpToken = findToken(LOOP, endToken);
        if(tokenMgr.getNextTokenKind() != LOOP) {
            iteration_scheme(node, tmpToken);
        }
        consumeToken(LOOP);
        endToken = findTokenInBlock(END, endToken);
        sequence_of_statements(node, endToken);

        consumeToken(END);
        consumeToken(LOOP);
        if(tokenMgr.getNextTokenKind() == identifier) {
            if(!hasLabel) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    /**
     * mode ::= <b>in | out | inout | buffer | linkage</b>
     */
    void mode(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODE);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(!(kind == IN || kind == OUT || kind == INOUT
                || kind == BUFFER || kind == LINKAGE)) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        consumeToken(kind);
        closeNodeScope(node);
    }
    

    /**
     * <dl> name ::=
     *   <dd> simple_name
     *   <br> | operator_symbol
     *   <br> | selected_name
     *   <br> | indexed_name
     *   <br> | slice_name
     *   <br> | attribute_name
     */
    void name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME);
        openNodeScope(node);
         
        Token lastPoint = findLastTokenInBlock(POINT, endToken);
        Token lastSQuote = findLastTokenInBlock(SQUOTE, endToken);
        Token lastLbracket = findLastLBracketToken(endToken);
        
       
        Token tmpToken = lastPoint;
        if(tmpToken == null || (lastSQuote != null 
               && checkLateComming(lastSQuote, tmpToken))) {
            tmpToken = lastSQuote;
        }
        if(tmpToken == null || (lastSQuote == null && lastLbracket != null 
                && checkLateComming(lastLbracket, tmpToken))) {
            tmpToken = lastLbracket;
        }
        
        if(tmpToken == null) {
            tmpToken = tokenMgr.getNextToken();
        }
        if(tmpToken == null) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        
        if(tmpToken.kind == string_literal) {
            operator_symbol(node, endToken);
        }else if(tmpToken.kind == POINT) {
            selected_name(node, endToken);
        }else if(tmpToken.kind == SQUOTE) {
            attribute_name(node, endToken);
        }else if(tmpToken.kind == LBRACKET) {
            if(lastLbracket == null) {
                throw new ParserException(tokenMgr.toNextToken());
            }
            Token nextToken = tokenMgr.getNextToken(lastLbracket);
            Token lastRbracket = findTokenInBlock(nextToken, RBRACKET, endToken);
            if(findTokenInBlock(nextToken, RANGE, lastRbracket) != null 
                || findTokenInBlock(nextToken, TO, lastRbracket)!= null
                || findTokenInBlock(nextToken, DOWNTO, lastRbracket)!= null) {
                slice_name(node, endToken);
            }else {
                indexed_name(node, endToken);
            }
        }else if(tmpToken.kind == identifier) {
            simple_name(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> nature_declaration ::=
     *   <dd> <b>nature</b> identifier <b>is</b> nature_definition ;
     */
    void nature_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNATURE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(NATURE);
        identifier(node, findToken(IS, endToken));
        consumeToken(IS);
        nature_definition(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, NATURE, this);
    }

    /**
     * <dl> nature_definition ::=
     *   <dd> scalar_nature_definition
     *   <br> | composite_nature_definition
     */
    void nature_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNATURE_DEFINITION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ARRAY || kind == RECORD) {
            composite_nature_definition(node, endToken);
        }else {
            scalar_nature_definition(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> nature_element_declaration ::=
     *   <dd> identifier_list : element_subnature_definition
     */
    void nature_element_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNATURE_ELEMENT_DECLARATION);
        openNodeScope(node);
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        element_subnature_definition(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> nature_mark ::=
     *   <dd> <i>nature_</i>name | <i>subnature_</i>name
     */
    void nature_mark(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNATURE_MARK);
        openNodeScope(node);
        name(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> next_statement ::=
     *   <dd> [ label : ] <b>next</b> [ <i>loop_</i>label ] [ <b>when</b> condition ] ;
     */
    void next_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNEXT_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        consumeToken(NEXT);
        new ASTtoken(node, tokenImage[NEXT]);
        if(tokenMgr.getNextTokenKind() == identifier) {
            label(node, endToken); //TODO check loop label
        }
        
        if(tokenMgr.getNextTokenKind() == WHEN) {
            consumeToken(WHEN);
            new ASTtoken(node, tokenImage[WHEN]);
            condition(node, endToken);
        }
        consumeToken(SEMICOLON);        
        closeNodeScope(node);
    }

    /**
     * <dl> null_statement ::=
     *   <dd> [ label : ] <b>null</b> ;
     */
    void null_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNULL_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        consumeToken(NULL);
        consumeToken(SEMICOLON); 
        closeNodeScope(node);
    }

    /**
     * <dl> numeric_literal ::=
     *   <dd> abstract_literal
     *   <br> | physical_literal
     */
    void numeric_literal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNUMERIC_LITERAL);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind(2) == identifier) {
            physical_literal(node, endToken);
        }else {
            abstract_literal(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> operator_symbol ::=
     *   <dd> string_literal
     */
    void operator_symbol(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOPERATOR_SYMBOL);
        openNodeScope(node);
        Token token = tokenMgr.toNextToken();
        new ASTtoken(node, token.image);
        closeNodeScope(node);
    }

    /**
     * <dl> options ::=
     *   <dd> [ <b>guarded</b> ] [ delay_mechanism ]
     */
    void options(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOPTIONS);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == GUARDED) {
            consumeToken(GUARDED);
            new ASTtoken(node, tokenImage[GUARDED]);
        }
        
        if(tokenMgr.getNextTokenKind() == TRANSPORT) {
            delay_mechanism(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> package_body ::=
     *   <dd> <b>package body</b> <i>package_</i>simple_name <b>is</b>
     *   <ul> package_body_declarative_part
     *   </ul> <b>end</b> [ <b>package body</b> ] [ <i>package_</i>simple_name ] ;
     */
    void package_body(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPACKAGE_BODY);
        openNodeScope(node);
        
        consumeToken(PACKAGE);
        consumeToken(BODY);
        endToken = findTokenInBlock(END, endToken);
        
        Token tmpToken = findToken(IS, endToken);
        simple_name(node, tmpToken);
        
        // save old symbol table
        SymbolTable oldTab = curSymbolTable;
        
        startBlock();
        ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        
        String name = ((ASTNode)node.getChild(0)).firstTokenImage();
        for(int i = 0; i < localUnits.size(); i++) {
            String tmpName = ((ASTNode)localUnits.get(i).getChild(0)).firstTokenImage();
            if(name.equalsIgnoreCase(tmpName)) {
                SymbolTable table = ((ASTNode)localUnits.get(i).getChild(0)).symTab;
                node.setSymbolTable(table);
                curSymbolTable.setParent(table);   // set parent as package
                break;
            }
        }
        
        consumeToken(IS);
        package_body_declarative_part(node, endToken);
        consumeToken(END);
        if(tokenMgr.getNextTokenKind() == PACKAGE) {
            consumeToken(PACKAGE);
            consumeToken(BODY);
        }
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        closeNodeScope(node);
        
        // restore symbol table
        curSymbolTable = oldTab;
    }

    /**
     * <dl> package_body_declarative_item ::=
     *   <dd> subprogram_declaration
     *   <br> | subprogram_body
     *   <br> | type_declaration
     *   <br> | subtype_declaration
     *   <br> | constant_declaration
     *   <br> | <i>shared_</i>variable_declaration
     *   <br> | file_declaration
     *   <br> | alias_declaration
     *   <br> | use_clause
     *   <br> | group_template_declaration
     *   <br> | group_declaration
     */
    void package_body_declarative_items(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        Token tmpToken = null, tmpToken1 = null;
        while(!exitLoop) {
            int kind = tokenMgr.getNextTokenKind();
            switch(kind)
            {
            case PROCEDURE:
            case FUNCTION:
            case IMPURE:
            case PURE:
                tmpToken = tokenMgr.getNextToken();
                if(kind == IMPURE || kind == PURE) {
                    tmpToken = tokenMgr.getNextToken(tmpToken);
                    if(tmpToken == null) {
                        throw new ParserException(tokenMgr.getNextToken());
                    }
                }
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    subprogram_body(p, endToken);
                }else {
                    subprogram_declaration(p, endToken);
                }
                if(kind == IMPURE || kind == PURE) {
                    kind = FUNCTION;
                }
                break;
            case GROUP:
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    group_template_declaration(p, endToken);
                }else {
                    group_declaration(p, endToken);
                }
                break;
            case SHARED:
                kind = VARIABLE;
            case VARIABLE:
                variable_declaration(p, endToken);
                break;
            case TYPE:
                type_declaration(p, endToken);
                break;
            case SUBTYPE:
                subtype_declaration(p, endToken);
                break;
            case CONSTANT:
                constant_declaration(p, endToken);
                break;
            case FILE:
                file_declaration(p, endToken);
                break;
            case ALIAS:
                alias_declaration(p, endToken);
                break;
            case USE:
                use_clause(p, endToken);
                break;
            default:    // not identified
                exitLoop = true;
                break;
            }
        }
    }

    /**
     * <dl> package_body_declarative_part ::=
     *   <dd> { package_body_declarative_item }
     */
    void package_body_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPACKAGE_BODY_DECLARATIVE_PART);
        openNodeScope(node);
        package_body_declarative_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> package_declaration ::=
     *   <dd> <b>package</b> identifier <b>is</b>
     *   <ul> package_declarative_part
     *   </ul> <b>end</b> [ <b>package</b> ] [ <i>package_</i>simple_name ] ;
     */
    void package_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPACKAGE_DECLARATION);
        openNodeScope(node);
        consumeToken(PACKAGE);
        endToken = findTokenInBlock(END, endToken);
        
        Token tmpToken = findToken(IS, endToken);
        identifier(node, tmpToken);
        consumeToken(IS);
        
        startBlock();
        ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        
        package_declarative_part(node, endToken);
        consumeToken(END);
        if(tokenMgr.getNextTokenKind() == PACKAGE) {
            consumeToken(PACKAGE);
        }
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        localUnits.add(node);
        closeNodeScope(node);
    }

    /**
     * <dl> package_declarative_item ::=
     *   <dd> subprogram_declaration
     *   <br> | type_declaration
     *   <br> | subtype_declaration
     *   <br> | constant_declaration
     *   <br> | signal_declaration
     *   <br> | <i>shared_</i>variable_declaration
     *   <br> | file_declaration
     *   <br> | alias_declaration
     *   <br> | component_declaration
     *   <br> | attribute_declaration
     *   <br> | attribute_specification
     *   <br> | disconnection_specification
     *   <br> | use_clause
     *   <br> | group_template_declaration
     *   <br> | group_declaration
     *   <br> | nature_declaration
     *   <br> | subnature_declaration
     *   <br> | terminal_declaration
     */
    void package_declarative_items(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        Token tmpToken = null, tmpToken1 = null;
        while(!exitLoop) {
            int kind = tokenMgr.getNextTokenKind();
            switch(kind)
            {
            case PROCEDURE:
            case FUNCTION:
            case IMPURE:
            case PURE:
                subprogram_declaration(p, endToken);
                if(kind == IMPURE || kind == PURE) {
                    kind = FUNCTION;
                }
                break;
            case ATTRIBUTE:
                tmpToken = findToken(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, tmpToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    attribute_specification(p, endToken);
                }else {
                    attribute_declaration(p, endToken);
                }
                break;
            case GROUP:
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    group_template_declaration(p, endToken);
                }else {
                    group_declaration(p, endToken);
                }
                break;
            case SHARED:
                kind = VARIABLE;
            case VARIABLE:
                variable_declaration(p, endToken);
                break;
            case TYPE:
                type_declaration(p, endToken);
                break;
            case SUBTYPE:
                subtype_declaration(p, endToken);
                break;
            case CONSTANT:
                constant_declaration(p, endToken);
                break;
            case SIGNAL:
                signal_declaration(p, endToken);
                break;
            case FILE:
                file_declaration(p, endToken);
                break;
            case ALIAS:
                alias_declaration(p, endToken);
                break;
            case DISCONNECT:
                disconnection_specification(p, endToken);
                break;
            case USE:
                use_clause(p, endToken);
                break;
            case NATURE:
                nature_declaration(p, endToken);
                break;
            case SUBNATURE:
                subnature_declaration(p, endToken);
                break;
            case TERMINAL:
                terminal_declaration(p, endToken);
                break;
            case COMPONENT:
                component_declaration(p, endToken);
                break;
            default:    // not identified
                exitLoop = true;
                break;
            }
        }
    }

    /**
     * <dl> package_declarative_part ::=
     *   <dd> { package_declarative_item }
     */
    void package_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPACKAGE_DECLARATIVE_PART);
        openNodeScope(node);
        package_declarative_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> parameter_specification ::=
     *   <dd> identifier <b>in</b> discrete_range
     */
    void parameter_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAMETER_SPECIFICATION);
        openNodeScope(node);
        identifier(node, endToken);
        consumeToken(IN);
        new ASTtoken(node, tokenImage[IN]);
        discrete_range(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> physical_literal ::=
     *   <dd> [ abstract_literal ] <i>unit_</i>name
     */
    void physical_literal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPHYSICAL_LITERAL);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == decimal_literal || kind == based_literal) {
            abstract_literal(node, endToken);
        }
        name(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> physical_type_definition ::=
     *   <dd> range_constraint
     *   <ul> <b>units</b>
     *   <ul> primary_unit_declaration
     *   <br> { secondary_unit_declaration }
     *   </ul> <b>end</b> <b>units</b> [ <i>physical_type_</i>simple_name ] </ul>
     */
    void physical_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPHYSICAL_TYPE_DEFINITION);
        openNodeScope(node);
        
        Token tmpToken = findToken(UNITS, endToken);
        range_constraint(node, tmpToken);
        consumeToken(UNITS);
        endToken = findTokenInBlock(END, endToken);
        
        primary_unit_declaration(node, endToken);
        
        while(true) {
            if(tokenMgr.getNextTokenKind() == END) {
                break;
            }
            secondary_unit_declaration(node, endToken);
        }
        consumeToken(END);
        consumeToken(UNITS);
        if(tokenMgr.getNextTokenKind() == identifier) {
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        closeNodeScope(node);
    }

    /**
     * <dl> port_clause ::=
     *   <dd> <b>port</b> ( port_list ) ;
     */
    void port_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT_CLAUSE);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(PORT);
        consumeToken(LBRACKET);
        Token tmpToken = findTokenInBlock(RBRACKET, endToken);
        port_list(node, tmpToken);
        consumeToken(RBRACKET);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> port_list ::=
     *   <dd> <i>port_</i>interface_list
     */
    void port_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT_LIST);
        openNodeScope(node);
        interface_list(node, endToken);
        closeNodeScope(node);
        SymbolParser.parseInterface_listKind(node, PORT, this);
    }

    /**
     * <dl> port_map_aspect ::=
     *   <dd> <b>port</b> <b>map</b> ( <i>port_</i>association_list )
     */
    void port_map_aspect(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT_MAP_ASPECT);
        openNodeScope(node);
        consumeToken(PORT);
        consumeToken(MAP);
        consumeToken(LBRACKET);
        Token tmpToken = findTokenInBlock(RBRACKET, endToken);
        association_list(node, tmpToken);
        consumeToken(RBRACKET);
        closeNodeScope(node);
    }

    /**
     * <dl> prefix ::=
     *   <dd> name
     *   <br> | function_call
     */
    void prefix(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPREFIX);
        openNodeScope(node);
        if(isFunction_call(node, endToken)) {
            function_call(node, endToken);
        }else {
            name(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> primary ::=
     *   <dd> name
     *   <br> | literal
     *   <br> | aggregate
     *   <br> | function_call
     *   <br> | qualified_expression
     *   <br> | type_conversion
     *   <br> | allocator
     *   <br> | ( expression )
     */
    void primary(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPRIMARY);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        Token tmpToken = null;
        
        if(kind == NEW) {
            allocator(node, endToken);
        }else if((tmpToken = findTokenInBlock(SQUOTE, endToken)) != null
                && kind != character_literal) {
            tmpToken = tokenMgr.getNextToken(tmpToken);
            if(tmpToken == null) {
                throw new ParserException(tokenMgr.toNextToken());
            }
            
            if(tmpToken.kind == LBRACKET) {
                qualified_expression(node, endToken);
            }else {
                name(node, endToken);   // attribute name
            }
        }else if(isType_conversion(node, endToken)) {
            type_conversion(node, endToken);
        }else if(isFunction_call(node, endToken)) {
            function_call(node, endToken);
        }else if(kind == character_literal || kind == decimal_literal
                || kind == based_literal || kind == string_literal
                || kind == bit_string_literal || kind == NULL) {
            literal(node, endToken);
        }else if(kind == LBRACKET) {
            Token tmp = findTokenInBlock(tokenMgr.getNextToken(2), COMMA, endToken);
            Token tmp1 = findTokenInBlock(tokenMgr.getNextToken(2), RARROW, endToken);
            if(tmp != null || tmp1 != null) {
                aggregate(node, endToken);
            }else {
                tokenMgr.toNextToken();     // "("
                expression(node, endToken.prev);
                tokenMgr.toNextToken();     // ")"
            }
        }else {
            name(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> primary_unit ::=
     *   <dd> entity_declaration
     *   <br> | configuration_declaration
     *   <br> | package_declaration
     */
    void primary_unit(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPRIMARY_UNIT);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        
        if(kind == ENTITY) {
            entity_declaration(node, endToken);
        }else if(kind == CONFIGURATION) {
            configuration_declaration(node, endToken);
        }else if(kind == PACKAGE) {
            package_declaration(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> primary_unit_declaration ::=
     *   <dd> identifier ;
     */
    void primary_unit_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPRIMARY_UNIT_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        identifier(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseCommonKind(node, UNITS, strVhdlType[TYPE_PHYSICAL], this);
    }

    /**
     * <dl> procedural_declarative_item ::=
     *   <dd> subprogram_declaration
     *   <br> | subprogram_body
     *   <br> | type_declaration
     *   <br> | subtype_declaration
     *   <br> | constant_declaration
     *   <br> | variable_declaration
     *   <br> | alias_declaration
     *   <br> | attribute_declaration
     *   <br> | attribute_specification
     *   <br> | use_clause
     *   <br> | group_template_declaration
     *   <br> | group_declaration
     */
    void procedural_declarative_items(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        Token tmpToken = null, tmpToken1 = null;
        while(!exitLoop) {
            int kind = tokenMgr.getNextTokenKind();
            switch(kind)
            {
            case PROCEDURE:
            case FUNCTION:
            case IMPURE:
            case PURE:
                tmpToken = tokenMgr.getNextToken();
                if(kind == IMPURE || kind == PURE) {
                    tmpToken = tokenMgr.getNextToken(tmpToken);
                    if(tmpToken == null) {
                        throw new ParserException(tokenMgr.getNextToken());
                    }
                }
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    subprogram_body(p, endToken);
                }else {
                    subprogram_declaration(p, endToken);
                }
                
                if(kind == IMPURE || kind == PURE) {
                    kind = FUNCTION;
                }
                break;
            case ATTRIBUTE:
                tmpToken = findToken(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, tmpToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    attribute_specification(p, endToken);
                }else {
                    attribute_declaration(p, endToken);
                }
                break;
            case GROUP:
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    group_template_declaration(p, endToken);
                }else {
                    group_declaration(p, endToken);
                }
                break;
            case SHARED:
                kind = VARIABLE;
            case VARIABLE:
                variable_declaration(p, endToken);
                break;
            case TYPE:
                type_declaration(p, endToken);
                break;
            case SUBTYPE:
                subtype_declaration(p, endToken);
                break;
            case CONSTANT:
                constant_declaration(p, endToken);
                break;
            case ALIAS:
                alias_declaration(p, endToken);
                break;
            case USE:
                use_clause(p, endToken);
                break;
            default:    // not identified
                exitLoop = true;
                break;
            }
        }
    }

    /**
     * <dl> procedural_declarative_part ::=
     *   <dd> { procedural_declarative_item }
     */
    void procedural_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCEDURAL_DECLARATIVE_PART);
        openNodeScope(node);
        procedural_declarative_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> procedural_statement_part ::=
     *   <dd> { sequential_statement }
     */
    void procedural_statement_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCEDURAL_STATEMENT_PART);
        openNodeScope(node);
        sequential_statements(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> procedure_call ::=
     *   <dd> <i>procedure_</i>name [ ( actual_parameter_part ) ]
     */
    void procedure_call(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCEDURE_CALL);
        openNodeScope(node);
        Token tmpToken = findLastLBracketToken(endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        name(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == LBRACKET) {
            consumeToken(LBRACKET);
            tmpToken = findTokenInBlock(RBRACKET, endToken);
            actual_parameter_part(node, tokenMgr.getNextToken(tmpToken));
            consumeToken(RBRACKET);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> procedure_call_statement ::=
     *   <dd> [ label : ] procedure_call ;
     */
    void procedure_call_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCEDURE_CALL_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        procedure_call(node, endToken);
        consumeToken(SEMICOLON); 
        closeNodeScope(node);
    }

    /**
     * <dl> process_declarative_item ::=
     *   <dd> subprogram_declaration
     *   <br> | subprogram_body
     *   <br> | type_declaration
     *   <br> | subtype_declaration
     *   <br> | constant_declaration
     *   <br> | variable_declaration
     *   <br> | file_declaration
     *   <br> | alias_declaration
     *   <br> | attribute_declaration
     *   <br> | attribute_specification
     *   <br> | use_clause
     *   <br> | group_template_declaration
     *   <br> | group_declaration
     */
    void process_declarative_items(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        Token tmpToken = null, tmpToken1 = null;
        while(!exitLoop) {
            int kind = tokenMgr.getNextTokenKind();
            switch(kind)
            {
            case PROCEDURE:
            case FUNCTION:
            case IMPURE:
            case PURE:
                tmpToken = tokenMgr.getNextToken();
                if(kind == IMPURE || kind == PURE) {
                    tmpToken = tokenMgr.getNextToken(tmpToken);
                    if(tmpToken == null) {
                        throw new ParserException(tokenMgr.getNextToken());
                    }
                }
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    subprogram_body(p, endToken);
                }else {
                    subprogram_declaration(p, endToken);
                }
                
                if(kind == IMPURE || kind == PURE) {
                    kind = FUNCTION;
                }
                break;
            case ATTRIBUTE:
                tmpToken = findToken(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, tmpToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    attribute_specification(p, endToken);
                }else {
                    attribute_declaration(p, endToken);
                }
                break;
            case GROUP:
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    group_template_declaration(p, endToken);
                }else {
                    group_declaration(p, endToken);
                }
                break;
            case SHARED:
                kind = VARIABLE;
            case VARIABLE:
                variable_declaration(p, endToken);
                break;
            case TYPE:
                type_declaration(p, endToken);
                break;
            case SUBTYPE:
                subtype_declaration(p, endToken);
                break;
            case CONSTANT:
                constant_declaration(p, endToken);
                break;
            case FILE:
                file_declaration(p, endToken);
                break;
            case ALIAS:
                alias_declaration(p, endToken);
                break;
            case USE:
                use_clause(p, endToken);
                break;
            default:    // not identified
                exitLoop = true;
                break;
            }
        }
    }

    /**
     * <dl> process_declarative_part ::=
     *   <dd> { process_declarative_item }
     */
    void process_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCESS_DECLARATIVE_PART);
        openNodeScope(node);
        process_declarative_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> process_statement ::=
     *   <dd> [ <i>process_</i>label : ]
     *   <ul> [ <b>postponed</b> ] <b>process</b> [ ( sensitivity_list ) ] [ <b>is</b> ]
     *   <ul> process_declarative_part
     *   </ul> <b>begin</b>
     *   <ul> process_statement_part
     *   </ul> <b>end</b> [ <b>postponed</b> ] <b>process</b> [ <i>process_</i>label ] ; </ul>
     */
    void process_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCESS_STATEMENT);
        openNodeScope(node);
        
        Token tmpToken = endToken;
        boolean hasLabel = false;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
            hasLabel = true;
        }
        startBlock();
        if(hasLabel) {
            ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        }
        
        if(tokenMgr.getNextTokenKind() == POSTPONED) {
            consumeToken(POSTPONED);
            new ASTtoken(node, tokenImage[POSTPONED]);
        }
        consumeToken(PROCESS);
        endToken = findTokenInBlock(END, endToken);
        
        if(tokenMgr.getNextTokenKind() == LBRACKET) {
            consumeToken(LBRACKET);
            tmpToken = findTokenInBlock(RBRACKET, endToken);
            sensitivity_list(node, tmpToken);
            consumeToken(RBRACKET);
        }
        if(tokenMgr.getNextTokenKind() == IS) {
            consumeToken(IS);
        }
        tmpToken = findTokenInBlock(BEGIN, endToken);
        process_declarative_part(node, endToken);
        consumeToken(BEGIN);
        
        process_statement_part(node, endToken);
        consumeToken(END);
        if(tokenMgr.getNextTokenKind() == POSTPONED) {
            consumeToken(POSTPONED);
        }
        consumeToken(PROCESS);
        if(tokenMgr.getNextTokenKind() == identifier) {
            if(!hasLabel) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        closeNodeScope(node);
    }

    /**
     * <dl> process_statement_part ::=
     *   <dd> { sequential_statement }
     */
    void process_statement_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCESS_STATEMENT_PART);
        openNodeScope(node);
        sequential_statements(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> qualified_expression ::=
     *   <dd> type_mark ' ( expression )
     *   <br> | type_mark ' aggregate
     */
    void qualified_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTQUALIFIED_EXPRESSION);
        openNodeScope(node);
        Token tmpToken = findToken(SQUOTE, endToken);
        type_mark(node, tmpToken);
        consumeToken(SQUOTE);
        
        tmpToken = tokenMgr.getNextToken(2);
        if(findTokenInBlock(tmpToken, COMMA, endToken) != null) {
            aggregate(node, endToken);
        }else {
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> quantity_declaration ::=
     *   <dd> free_quantity_declaration
     *   <br> | branch_quantity_declaration
     *   <br> | source_quantity_declaration
     */
    void quantity_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTQUANTITY_DECLARATION);
        openNodeScope(node);
        if(findToken(SPECTRUM, endToken) != null 
                || findToken(NOISE, endToken) != null) {
            source_quantity_declaration(node, endToken);
        }else if(findToken(ACROSS, endToken) != null 
                || findToken(THROUGH, endToken) != null
                || findToken(TO, endToken) != null) {
            branch_quantity_declaration(node, endToken);
        }else {
            free_quantity_declaration(node, endToken);
        }
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node,QUANTITY, this);
    }

    /**
     * <dl> quantity_list ::=
     *   <dd> <i>quantity_</i>name { , <i>quantity_</i>name }
     *   <br> | <b>others</b>
     *   <br> | <b>all</b>
     */
    void quantity_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTQUANTITY_LIST);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == OTHERS || kind == ALL) {
            consumeToken(kind);
            new ASTtoken(node, tokenImage[kind]);
        }else if(kind == identifier) {
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                name(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
        }
        closeNodeScope(node);
    }

    /**
     * <dl> quantity_specification ::=
     *   <dd> quantity_list : type_mark
     */
    void quantity_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTQUANTITY_SPECIFICATION);
        openNodeScope(node);
        Token tmpToken = findToken(COLON, endToken);
        quantity_list(node, tmpToken);
        consumeToken(COLON);
        type_mark(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> range ::=
     *   <dd> <i>range_</i>attribute_name
     *   <br> | simple_expression direction simple_expression
     */
    void range(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRANGE);
        openNodeScope(node);
        if(findTokenInBlock(TO, endToken) != null
            || findToken(DOWNTO, endToken) != null) {
            Token tmpToken = findToken(TO, endToken);
            if(tmpToken == null)
                tmpToken = findToken(DOWNTO, endToken);
            simple_expression(node, tmpToken);
            direction(node, endToken);
            simple_expression(node, endToken);
        }else {
            attribute_name(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> range_constraint ::=
     *   <dd> <b>range</b> range
     */
    void range_constraint(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRANGE_CONSTRAINT);
        openNodeScope(node);
        consumeToken(RANGE);
        range(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> record_nature_definition ::=
     *   <dd> <b>record</b>
     *   <ul> nature_element_declaration
     *   <br> { nature_element_declaration }
     *   </ul><b>end</b> <b>record</b> [ <i>record_nature_</i>simple_name ]
     */
    void record_nature_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRECORD_NATURE_DEFINITION);
        openNodeScope(node);        
        consumeToken(RECORD);
        endToken = findTokenInBlock(END, endToken);
        
        while(true) {
            nature_element_declaration(node, endToken);
            int kind = tokenMgr.getNextTokenKind();
            if(kind < 0 || kind == END) {
                break;
            }
        }
        consumeToken(END);
        consumeToken(RECORD);
        if(tokenMgr.getNextTokenKind() == identifier) {
            tokenMgr.toNextToken();
        }
        closeNodeScope(node);
    }

    /**
     * <dl> record_type_definition ::=
     *   <dd> <b>record</b>
     *   <ul> element_declaration
     *   <br> { element_declaration }
     *   </ul><b>end</b> <b>record</b> [ <i>record_type_</i>simple_name ]
     */
    void record_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRECORD_TYPE_DEFINITION);
        openNodeScope(node);        
        consumeToken(RECORD);
        endToken = findTokenInBlock(END, endToken);
        
        while(true) {
            element_declaration(node, endToken);
            int kind = tokenMgr.getNextTokenKind();
            if(kind < 0 || kind == END) {
                break;
            }
        }
        consumeToken(END);
        consumeToken(RECORD);
        if(tokenMgr.getNextTokenKind() == identifier) {
            tokenMgr.toNextToken();
        }
        closeNodeScope(node);
    }

    /**
     * <dl> relation ::=
     *   <dd> shift_expression [ relational_operator shift_expression ]
     */
    void relation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRELATION);
        openNodeScope(node);
        Token relToken = endToken;
        Token tmpToken = null;
        if((tmpToken = getNextRelational_operator(endToken)) != null) {
            relToken = tmpToken;
        }
        shift_expression(node, relToken);
        if(relToken != endToken) {
            relational_operator(node, endToken);
            shift_expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> relational_operator ::=
     *   <dd> = | /= | < | <= | > | >=
     */
    void relational_operator(IASTNode p, Token endToken) throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(!(kind == EQ || kind == NEQ || kind == LO || kind == LE
                || kind == GT || kind == GE)) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        consumeToken(kind);
        new ASTtoken(p, tokenImage[kind]);
    }

    /**
     * <dl> report_statement ::=
     *   <dd>  [ label : ]
     *   <ul> <b>report</b> expression
     *   <ul> [ <b>severity</b> expression ] ; </ul></ul>
     */
    void report_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREPORT_STATEMENT);
        openNodeScope(node);
        
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
        }
        consumeToken(REPORT);
        new ASTtoken(node, tokenImage[REPORT]);
        endToken = findToken(SEMICOLON, endToken);
        
        tmpToken = findToken(SEVERITY, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        expression(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == SEVERITY) {
            consumeToken(SEVERITY);
            new ASTtoken(node, tokenImage[SEVERITY]);
            expression(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> return_statement ::=
     *   <dd> [ label : ] <b>return</b> [ expression ] ;
     */
    void return_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRETURN_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
        }
        consumeToken(RETURN);
        if(tokenMgr.getNextTokenKind() != SEMICOLON) {
            expression(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> scalar_nature_definition ::=
     *   <dd> type_mark <b>across</b>
     *   <dd> type_mark <b>through</b>
     *   <dd> identifier <b>reference</b>
     */
    void scalar_nature_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSCALAR_NATURE_DEFINITION);
        openNodeScope(node);
        type_mark(node, endToken);
        Token token = tokenMgr.toNextToken();
        new ASTtoken(node, token.image);
        closeNodeScope(node);
    }

    /**
     * <dl> scalar_type_definition ::=
     *   <dd> enumeration_type_definition
     *   <br> | integer_type_definition
     *   <br> | floating_type_definition
     *   <br> | physical_type_definition
     */
    void scalar_type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSCALAR_TYPE_DEFINITION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == LBRACKET) {
            enumeration_type_definition(node, endToken);
        }else if(kind == RANGE) {
            if(findToken(UNITS, endToken) != null) {
                physical_type_definition(node, endToken);
            }else {
                integer_type_definition(node, endToken); // the same as floating_type_definition
            }
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> secondary_unit ::=
     *   <dd> architecture_body
     *   <br> | package_body
     */
    void secondary_unit(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSECONDARY_UNIT);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        
        if(parseSymbol) {
            // if only parse symbol, just ignore it
            // because we need only package declaration
            endToken = findTokenInBlock(tokenMgr.getNextToken(2), END, endToken);
            Token semToken = findToken(endToken, SEMICOLON, null);
            if(semToken == null) {
                throw new ParserException(tokenMgr.getNextToken());
            }
            tokenMgr.setCurrentToken(semToken);
            closeNodeScope(node);
            return;
        }
        
        if(kind == ARCHITECTURE) {
            architecture_body(node, endToken);
        }else {
            package_body(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> secondary_unit_declaration ::=
     *   <dd> identifier = physical_literal ;
     */
    void secondary_unit_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSECONDARY_UNIT_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        identifier(node, endToken);
        consumeToken(EQ);
        physical_literal(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseCommonKind(node, UNITS, strVhdlType[TYPE_PHYSICAL], 
                        lastNode.getName(), this);
    }

    /**
     * <dl> selected_name ::=
     *   <dd> prefix . suffix
     */
    void selected_name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSELECTED_NAME);
        openNodeScope(node);
        if(endToken == null)
            throw new ParserException(tokenMgr.getNextToken());
        Token tmpToken = findLastTokenInBlock(POINT, endToken);
        if(tmpToken == null)
            throw new ParserException(tokenMgr.getNextToken());
        prefix(node, tmpToken);
        consumeToken(POINT);
        suffix(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> selected_signal_assignment ::=
     *   <dd> <b>with</b> expression <b>select</b>
     *   <ul> target <= options selected_waveforms ; </ul>
     */
    void selected_signal_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSELECTED_SIGNAL_ASSIGNMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(WITH);
        new ASTtoken(node, tokenImage[WITH]);
        Token tmpToken = findToken(SELECT, endToken);
        expression(node, tmpToken);
        consumeToken(SELECT);
        new ASTtoken(node, tokenImage[SELECT]);
        
        tmpToken = findToken(LE, endToken);
        target(node, tmpToken);
        consumeToken(LE);
        new ASTtoken(node, tokenImage[LE]);
        options(node, endToken);
        selected_waveforms(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> selected_waveforms ::=
     *   <dd> { waveform <b>when</b> choices , }
     *   <br> waveform <b>when</b> choices
     */
    void selected_waveforms(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSELECTED_WAVEFORMS);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findToken(WHEN, endToken);
            waveform(node, tmpToken);
            consumeToken(WHEN);
            
            tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            choices(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> sensitivity_clause ::=
     *   <dd> <b>on</b> sensitivity_list
     */
    void sensitivity_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSENSITIVITY_CLAUSE);
        openNodeScope(node);
        consumeToken(ON);
        sensitivity_list(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> sensitivity_list ::=
     *   <dd> <i>signal_</i>name { , <i>signal_</i>name }
     */
    void sensitivity_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSENSITIVITY_LIST);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            name(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> sequence_of_statements ::=
     *   <dd> { sequential_statement }
     */
    void sequence_of_statements(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSEQUENCE_OF_STATEMENTS);
        openNodeScope(node);
        sequential_statements(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> sequential_statement ::=
     *   <dd> wait_statement
     *   <br> | assertion_statement
     *   <br> | report_statement
     *   <br> | signal_assignment_statement
     *   <br> | variable_assignment_statement
     *   <br> | procedure_call_statement
     *   <br> | if_statement
     *   <br> | case_statement
     *   <br> | loop_statement
     *   <br> | next_statement
     *   <br> | exit_statement
     *   <br> | return_statement
     *   <br> | null_statement
     *   <br> | break_statement
     */
    void sequential_statements(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        while(!exitLoop) {
            Token tmpToken = tokenMgr.getNextToken();
            boolean handled = false;
            while(tmpToken != endToken) {
                if(tmpToken == null) { return; }
                handled = false;
                if(tmpToken.kind == IF) {
                    tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), END, endToken);
                    if(tmpToken == null) { return; }
                    tmpToken = findToken(tmpToken, SEMICOLON, endToken);
                    if_statement(p, tmpToken);
                    handled = true;
                    break;
                }else if(tmpToken.kind == LOOP) {
                    tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), END, endToken);
                    if(tmpToken == null) { return; }
                    tmpToken = findToken(tmpToken, SEMICOLON, endToken);
                    loop_statement(p, tmpToken);
                    handled = true;
                    break;
                }else if(tmpToken.kind == CASE) {
                    tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), END, endToken);
                    if(tmpToken == null) { return; }
                    tmpToken = findToken(tmpToken, SEMICOLON, endToken);
                    case_statement(p, tmpToken);
                    handled = true;
                    break;
                }else if(tmpToken.kind == SEMICOLON) {
                    break;
                }
                tmpToken = tokenMgr.getNextToken(tmpToken);
            }
            
            if(tmpToken == null) { return; }            
            if(handled) { continue; }

            if(findTokenInBlock(ASSERT, tmpToken) != null) {
                assertion_statement(p, endToken);
            }else if(findTokenInBlock(EXIT, tmpToken) != null) {
                exit_statement(p, endToken);
            }else if(findTokenInBlock(NEXT, tmpToken) != null) {
                next_statement(p, endToken);
            }else if(tokenMgr.getNextTokenKind() == NULL || (tokenMgr.getNextTokenKind() == identifier 
                        && tokenMgr.getNextTokenKind(2) == NULL)) {
                null_statement(p, endToken);
            }else if(findTokenInBlock(REPORT, tmpToken) != null) {
                report_statement(p, endToken);
            }else if(findTokenInBlock(RETURN, tmpToken) != null) {
                return_statement(p, endToken);
            }else if(findTokenInBlock(WAIT, tmpToken) != null) {
                wait_statement(p, endToken);
            }else if(findTokenInBlock(ASSIGN, tmpToken) != null) {
                variable_assignment_statement(p, endToken);
            }else if(findTokenInBlock(LE, tmpToken) != null) {
                signal_assignment_statement(p, endToken);
            }else if(findTokenInBlock(BREAK, tmpToken) != null){
                break_statement(p, endToken);
            }else if(tokenMgr.getNextTokenKind() == identifier){
                procedure_call_statement(p, endToken);
            }else {
                exitLoop = true;
            }
        }
    }

    /**
     * <dl> shift_expression ::=
     *   <dd> simple_expression [ shift_operator simple_expression ]
     */
    void shift_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSHIFT_EXPRESSION);
        openNodeScope(node);
        
        Token relToken = endToken;
        Token tmpToken = null;
        if((tmpToken = getNextShift_operator(endToken)) != null) {
            relToken = tmpToken;
        }
        
        simple_expression(node, relToken);
        if(relToken != endToken) {
            shift_operator(node, endToken);
            simple_expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> shift_operator ::=
     *   <dd> <b>sll</b> | <b>srl</b> | <b>sla</b> | <b>sra</b> | <b>rol</b> | <b>ror</b>
     */
    void shift_operator(IASTNode p, Token endToken) throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(!(kind == SLL || kind == SRL || kind == SLA || kind == SRA
                || kind == ROL || kind == ROR)) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        consumeToken(kind);
        new ASTtoken(p, tokenImage[kind]);
    }

    /**
     * <dl> sign ::=
     *   <dd> + | -
     */
    void sign(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIGN);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(!(kind == ADD || kind == SUB)) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        consumeToken(kind);
        new ASTtoken(node, tokenImage[kind]);
        closeNodeScope(node);
    }

    /**
     * <dl> signal_assignment_statement ::=
     *   <dd> [ label : ] target <= [ delay_mechanism ] waveform ;
     */
    void signal_assignment_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIGNAL_ASSIGNMENT_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
        }
        
        tmpToken = findToken(LE, endToken);
        target(node, tmpToken);
        consumeToken(LE);
        new ASTtoken(node, tokenImage[LE]);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == TRANSPORT || kind == REJECT) {
            delay_mechanism(node, endToken);
        }
        waveform(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> signal_declaration ::=
     *   <dd> <b>signal</b> identifier_list : subtype_indication [ signal_kind ] [ := expression ] ;
     */
    void signal_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIGNAL_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = findToken(COLON, endToken);
        consumeToken(SIGNAL);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        
        tmpToken = findToken(ASSIGN, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        subtype_indication(node, tmpToken);
        
        int kind = tokenMgr.getNextTokenKind();
        if(kind == REGISTER || kind == BUS) {
            signal_kind(node, tmpToken);
        }
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            new ASTtoken(node, tokenImage[ASSIGN]);
            expression(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, SIGNAL, this);
    }

    /**
     * <dl> signal_kind ::=
     *   <dd> <b>register</b> | <b>bus</b>
     */
    void signal_kind(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIGNAL_KIND);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(!(kind == REGISTER || kind == BUS)) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        consumeToken(kind);
        new ASTtoken(node, tokenImage[kind]);
        closeNodeScope(node);
    }

    /**
     * <dl> signal_list ::=
     *   <dd> <i>signal_</i>name { , <i>signal_</i>name }
     *   <br> | <b>others</b>
     *   <br> | <b>all</b>
     */
    void signal_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIGNAL_LIST);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == OTHERS || kind == ALL) {
            consumeToken(kind);
            new ASTtoken(node, tokenImage[kind]);
        }else if(kind == identifier) {
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                name(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
        }
        closeNodeScope(node);
    }

    /**
     * <dl> signature ::=
     *   <dd> [ [ type_mark { , type_mark } ] [ <b>return</b> type_mark ] ]
     */
    void signature(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIGNATURE);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == identifier) {
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                type_mark(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
        }
        if(tokenMgr.getNextTokenKind() == RETURN) {
            consumeToken(RETURN);
            new ASTtoken(node, tokenImage[RETURN]);
            type_mark(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> simple_expression ::=
     *   <dd> [ sign ] term { adding_operator term }
     */
    void simple_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMPLE_EXPRESSION);
        openNodeScope(node);
        
        //TODO special handle, decrease level of tree
        if(tokenMgr.getNextToken(2) == endToken) {
            // only one token, regard as literal
            literal(node, endToken);
            closeNodeScope(node);
            return;
        }
        
        //TODO: ????
        if(node.isDescendantOf(ASTRANGE) 
                && tokenMgr.getNextToken(2).image.equalsIgnoreCase("range")) {
            tokenMgr.toNextToken();
            tokenMgr.toNextToken();
        }
        
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ADD || kind == SUB) {
            sign(node, endToken);
        }
        while(true) {
            Token tmpToken = getNextAdding_operator(endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            term(node, tmpToken);
            kind = tokenMgr.getNextTokenKind();
            if(tokenMgr.getNextToken() == endToken
                   || !(kind == ADD || kind == SUB || kind == CONCAT)){
                break;
            }
            consumeToken(kind);
            new ASTtoken(node, tokenImage[kind]);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> simple_name ::=
     *   <dd> identifier
     */
    void simple_name(IASTNode p, Token endToken) throws ParserException {
        identifier(p, endToken);
    }

    /**
     * <dl> simple_simultaneous_statement ::=
     *   <dd> [ label : ] simple_expression == simple_expression [ tolerance_aspect ] ;
     */
    void simple_simultaneous_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMPLE_SIMULTANEOUS_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
        }
        
        tmpToken = findToken(EQ2, endToken);
        simple_expression(node, tmpToken);
        consumeToken(EQ2);
        simple_expression(node, endToken);
        if(tokenMgr.getNextTokenKind() == TOLERANCE) {
            tolerance_aspect(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> simultaneous_alternative ::=
     *   <dd> <b>when</b> choices =>
     *   <ul> simultaneous_statement_part </ul>
     */
    void simultaneous_alternative(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMULTANEOUS_ALTERNATIVE);
        openNodeScope(node);
        consumeToken(WHEN);
        Token tmpToken = findToken(RARROW, endToken);
        choices(node, tmpToken);
        consumeToken(RARROW);
        simultaneous_statement_part(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> simultaneous_case_statement ::=
     *   <dd> [ <i>case_</i>label : ]
     *   <ul> <b>case</b> expression <b>use</b>
     *   <ul> simultaneous_alternative
     *   <br> { simultaneous_alternative }
     *   </ul> <b>end</b> <b>case</b> [ <i>case_</i>label ] ; </ul>
     */
    void simultaneous_case_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMULTANEOUS_CASE_STATEMENT);
        openNodeScope(node);
        
        boolean hasLabel = false;
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
            hasLabel = true;
        }
        consumeToken(CASE);
        endToken = findTokenInBlock(END, endToken);
        
        tmpToken = findToken(USE, endToken);
        expression(node, tmpToken);
        consumeToken(USE);
        while(true) {
            tmpToken = findToken(WHEN, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            simultaneous_alternative(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != WHEN) {
                break;
            }
        }
        consumeToken(END);
        consumeToken(CASE);
        if(tokenMgr.getNextTokenKind() == identifier) {
            if(!hasLabel) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> simultaneous_if_statement ::=
     *   <dd> [ <i>if_</i>label : ]
     *   <ul> <b>if</b> condition <b>use</b>
     *   <ul>  simultaneous_statement_part
     *   </ul> { <b>elsif</b> condition <b>then</b>
     *   <ul> simultaneous_statement_part }
     *   </ul> [ <b>else</b>
     *   <ul> simultaneous_statement_part ]
     *   </ul> <b>end</b> <b>use</b> [ <i>if_</i>label ] ; </ul>
     */
    void simultaneous_if_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMULTANEOUS_IF_STATEMENT);
        openNodeScope(node);
        
        boolean hasLabel = false;
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
            hasLabel = true;
        }
        consumeToken(IF);
        new ASTtoken(node, tokenImage[IF]);
        endToken = findTokenInBlock(END, endToken);
         
        tmpToken = findToken(USE, endToken);
        condition(node, tmpToken);
        consumeToken(USE);
        
        while(true) {
            tmpToken = findToken(ELSIF, endToken);
            if(tmpToken == null)
                tmpToken = findToken(ELSE, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            simultaneous_statement_part(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != ELSIF) {
                break;
            }
            consumeToken(ELSIF);
            new ASTtoken(node, tokenImage[ELSIF]);
            
            tmpToken = findToken(THEN, endToken);
            condition(node, tmpToken);
            consumeToken(THEN);
        }
        if(tokenMgr.getNextTokenKind() == ELSE) {
            consumeToken(ELSE);
            new ASTtoken(node, tokenImage[ELSE]);
            simultaneous_statement_part(node, endToken);
        }
        
        consumeToken(END);
        consumeToken(USE);
        if(tokenMgr.getNextTokenKind() == identifier) {
            if(!hasLabel) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> simultaneous_null_statement ::=
     *   <dd> [ label : ] <b>null</b> ;
     */
    void simultaneous_null_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMULTANEOUS_NULL_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            identifier(node, endToken);   // label
            consumeToken(COLON);
        }
        consumeToken(NULL);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> simultaneous_procedural_statement ::=
     *   <dd> [ <i>procedural_</i>label : ]
     *   <ul> <b>procedural</b> [ <b>is</b> ]
     *   <ul> procedural_declarative_part
     *   </ul> <b>begin</b>
     *   <ul> procedural_statement_part
     *   </ul> <b>end</b> <b>procedural</b> [ <i>procedural_</i>label ] ; </ul>
     */
    void simultaneous_procedural_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMULTANEOUS_PROCEDURAL_STATEMENT);
        openNodeScope(node);
        
        boolean hasLabel = false;
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
            hasLabel = true;
        }
        consumeToken(PROCEDURAL);
        endToken = findTokenInBlock(END, endToken);
        
        startBlock();
        if(hasLabel) {
            ((ASTNode)node.getChild(0)).setSymbolTable(curSymbolTable);
        }
        
        if(tokenMgr.getNextTokenKind() == IS) {
            consumeToken(IS);
        }
        
        tmpToken = findTokenInBlock(BEGIN, endToken);
        procedural_declarative_part(node, tmpToken);
        consumeToken(BEGIN);
        procedural_statement_part(node, endToken);
        consumeToken(END);
        if(tokenMgr.getNextTokenKind() == POSTPONED) {
            consumeToken(POSTPONED);
        }
        consumeToken(PROCEDURAL);
        if(tokenMgr.getNextTokenKind() == identifier) {
            if(!hasLabel) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            Token t1 = ((ASTNode)node.getChild(0)).getFirstToken();
            Token t2 = tokenMgr.toNextToken();
            if(!t1.image.equalsIgnoreCase(t2.image)) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
        }
        consumeToken(SEMICOLON);
        endBlock();
        closeNodeScope(node);
    }

    /**
     * <dl> simultaneous_statement ::=
     *   <dd> simple_simultaneous_statement
     *   <br> | simultaneous_if_statement
     *   <br> | simultaneous_case_statement
     *   <br> | simultaneous_procedural_statement
     *   <br> | simultaneous_null_statement
     *   @return
     *       true: this node has been handled, false elsewhere
     */
    boolean simultaneous_statement(IASTNode p, Token endToken) throws ParserException {
        Token tmpToken = tokenMgr.getNextToken();
        if(tmpToken == null) { return false; }
        if(tmpToken.kind == SEMICOLON || tmpToken.kind == BEGIN
                || tmpToken.kind == END) {
            return false;
        }
        
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = tokenMgr.getNextToken(tmpToken);   // ignore label
            tmpToken = tokenMgr.getNextToken(tmpToken);
            if(tmpToken == null) { return false; }
        }
        
        ASTNode node = null;
        Token tmpToken1 = null;
        
        if(tmpToken.kind == IF) {
            tmpToken1 = findToken(tmpToken, SEMICOLON, endToken);
            if(tmpToken1 != null) {
                tmpToken1 = findToken(tmpToken, USE, tmpToken1);
            }
            if(tmpToken1 == null) {
                return false;
            }
        }
        
        if(!(tmpToken.kind == IF || tmpToken.kind == PROCEDURAL 
                || tmpToken.kind == CASE || tmpToken.kind == NULL)) {
            tmpToken1 = findToken(tmpToken, SEMICOLON, endToken);
            if(tmpToken1 != null) {
                tmpToken1 = findToken(tmpToken, EQ2, tmpToken1);
            }
            if(tmpToken1 == null) {
                return false;
            }
        }

        node = new ASTNode(p, ASTSIMULTANEOUS_STATEMENT);
        openNodeScope(node);

        switch(tmpToken.kind)
        {
        case IF:
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), END, endToken);
            if(tmpToken == null) { return false; }
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            simultaneous_if_statement(node, tmpToken);
            break;
        case PROCEDURAL:
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), END, endToken);
            if(tmpToken == null) { return false; }
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            simultaneous_procedural_statement(node, tmpToken);
            break;
        case CASE:
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken),
                            END, endToken);
            if(tmpToken == null) { return false; }
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            simultaneous_case_statement(node, tmpToken);
            break;
        case NULL:
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            simultaneous_null_statement(node, endToken);
            break;
        default:
            tmpToken = findToken(tmpToken, SEMICOLON, endToken);
            simple_simultaneous_statement(node, endToken);
            break;
        }
        
        closeNodeScope(node);
        return true;
    }

    /**
     * <dl> simultaneous_statement_part ::=
     *   <dd> { simultaneous_statement }
     */
    void simultaneous_statement_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMULTANEOUS_STATEMENT_PART);
        openNodeScope(node);
        while(simultaneous_statement(node, endToken));
        closeNodeScope(node);
    }

    /**
     * <dl> slice_name ::=
     *   <dd> prefix ( discrete_range )
     */
    void slice_name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSLICE_NAME);
        openNodeScope(node);
        Token tk = findLastLBracketToken(endToken);
        prefix(node, tk);
        consumeToken(LBRACKET);
        discrete_range(node, endToken.prev);
        consumeToken(RBRACKET);
        closeNodeScope(node);
    }

    /**
     * <dl> source_aspect ::=
     *   <dd> <b>spectrum</b> <i>magnitude_</i>simple_expression , <i>phase_</i>simple_expression
     *   <br> | <b>noise</b> <i>power_</i>simple_expression
     */
    void source_aspect(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSOURCE_ASPECT);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == SPECTRUM) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            consumeToken(SPECTRUM);
            simple_expression(node, tmpToken);    // magnitude
            consumeToken(COMMA);
            new ASTtoken(node, tokenImage[COMMA]);
            simple_expression(node, endToken);    // phase
        }else {
            consumeToken(NOISE);
            simple_expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> source_quantity_declaration ::=
     *   <dd> <b>quantity</b> identifier_list : subtype_indication source_aspect ;
     */
    void source_quantity_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSOURCE_QUANTITY_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(QUANTITY);
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        subtype_indication(node, endToken);
        source_aspect(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> step_limit_specification ::=
     *   <dd> <b>limit</b> quantity_specification <b>with</b> <i>real_</i>expression ;
     */
    void step_limit_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTEP_LIMIT_SPECIFICATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(LIMIT);
        Token tmpToken = findToken(WITH, endToken);
        quantity_specification(node, tmpToken);
        consumeToken(WITH);
        expression(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, LIMIT, this);
    }

    /**
     * <dl> subnature_declaration ::=
     *   <dd> <b>subnature</b> identifier <b>is</b> subnature_indication ;
     */
    void subnature_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBNATURE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(SUBNATURE);
        Token tmpToken = findToken(IS, endToken);
        identifier(node, tmpToken);
        consumeToken(IS);
        subnature_indication(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, SUBNATURE, this);
    }

    /**
     * <dl> subnature_indication ::=
     *   <dd> nature_mark [ index_constraint ] 
     *   [ <b>tolerance</b> <i>string_</i>expression 
     *      <b>across</b> <i>string_</i>expression <b>through</b> ]
     */
    void subnature_indication(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBNATURE_INDICATION);
        openNodeScope(node);
        
        Token tmpToken = findToken(TOLERANCE, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        nature_mark(node, tmpToken);
        if(findToken(RANGE, endToken) != null
            || findToken(TO, endToken) != null
            || findToken(DOWNTO, endToken) != null) {
            index_constraint(node, tmpToken);
        }
        if(tokenMgr.getNextTokenKind() == TOLERANCE) {
            consumeToken(TOLERANCE);
            new ASTtoken(node, tokenImage[TOLERANCE]);
            
            tmpToken = findToken(ACROSS, endToken); 
            expression(node, tmpToken);
            
            consumeToken(ACROSS);
            new ASTtoken(node, tokenImage[ACROSS]);
            expression(node, endToken);
            consumeToken(THROUGH);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> string_literal ::=
     *   <dd> " { graphic_character } "
     */
    //void string_literal(Node p, Token endToken) throws ParseException {
    //}

    /**
     * <dl> subprogram_body ::=
     *   <dd> subprogram_specification <b>is</b>
     *   <ul> subprogram_declarative_part
     *   </ul> <b>begin</b>
     *   <ul> subprogram_statement_part
     *   </ul> <b>end</b> [ subprogram_kind ] [ designator ] ;
     *   <br>=========================
     *   <br><br><dl> subprogram_kind ::=
     *   <dd> <b>procedure</b> | <b>function</b>
     */
    void subprogram_body(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBPROGRAM_BODY);
        openNodeScope(node);
        
        int subkind = -1;
        if(tokenMgr.getNextTokenKind() == PROCEDURE)
            subkind = PROCEDURE;
        else
            subkind = FUNCTION;

        startBlock();
        Token tmpToken = findToken(identifier, endToken);
        curSymbolTable.setName(tmpToken.image);
        node.setName(tmpToken.image);
        
        tmpToken = findToken(IS, endToken);
        subprogram_specification(node, tmpToken);
        endToken = findTokenInBlock(END, endToken);
        
        
        // add parameter list symbols to subprogram body
        ASTNode param_list = (ASTNode)node.getDescendant(ASTFORMAL_PARAMETER_LIST);
        if(param_list != null)
            curSymbolTable.addAll(param_list.getSymbolTable());
        
        consumeToken(IS);
        
        tmpToken = findTokenInBlock(BEGIN, endToken);
        subprogram_declarative_part(node, tmpToken);
        consumeToken(BEGIN);
        
        subprogram_statement_part(node, endToken);
        consumeToken(END);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == PROCEDURE || kind == FUNCTION) {
            if(subkind != kind) {
                throw new ParserException(tokenMgr.toNextToken());
            }
            consumeToken(kind);
        }
        
        endToken = findToken(SEMICOLON, endToken);
        kind = tokenMgr.getNextTokenKind();
        if(kind == identifier || kind == string_literal) {
            designator(node, endToken);
        }
        consumeToken(SEMICOLON);
        endBlock();
        closeNodeScope(node);
    }

    /**
     * <dl> subprogram_declaration ::=
     *   <dd> subprogram_specification ;
     */
    void subprogram_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBPROGRAM_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        subprogram_specification(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> subprogram_declarative_item ::=
     *   <dd> subprogram_declaration
     *   <br> | subprogram_body
     *   <br> | type_declaration
     *   <br> | subtype_declaration
     *   <br> | constant_declaration
     *   <br> | variable_declaration
     *   <br> | file_declaration
     *   <br> | alias_declaration
     *   <br> | attribute_declaration
     *   <br> | attribute_specification
     *   <br> | use_clause
     *   <br> | group_template_declaration
     *   <br> | group_declaration
     */
    void subprogram_declarative_items(IASTNode p, Token endToken) throws ParserException {
        boolean exitLoop = false;
        Token tmpToken = null, tmpToken1 = null;
        while(!exitLoop) {
            int kind = tokenMgr.getNextTokenKind();
            switch(kind)
            {
            case PROCEDURE:
            case FUNCTION:
            case IMPURE:
            case PURE:
                tmpToken = tokenMgr.getNextToken();
                if(kind == IMPURE || kind == PURE) {
                    tmpToken = tokenMgr.getNextToken(tmpToken);
                    if(tmpToken == null) {
                        throw new ParserException(tokenMgr.getNextToken());
                    }
                }
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    subprogram_body(p, endToken);
                }else {
                    subprogram_declaration(p, endToken);
                }
                
                if(kind == IMPURE || kind == PURE) {
                    kind = FUNCTION;
                }
                break;
            case ATTRIBUTE:
                tmpToken = findToken(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, tmpToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    attribute_specification(p, endToken);
                }else {
                    attribute_declaration(p, endToken);
                }
                break;
            case GROUP:
                tmpToken = findTokenInBlock(tokenMgr.getNextToken(), SEMICOLON, endToken);
                if(tmpToken == null) {
                    throw new ParserException(tokenMgr.getNextToken());
                }
                tmpToken1 = findToken(IS, endToken);
                if(tmpToken1 != null && checkLateComming(tmpToken, tmpToken1)) {
                    group_template_declaration(p, endToken);
                }else {
                    group_declaration(p, endToken);
                }
                break;
            case SHARED:
                kind = VARIABLE;
            case VARIABLE:
                variable_declaration(p, endToken);
                break;
            case TYPE:
                type_declaration(p, endToken);
                break;
            case SUBTYPE:
                subtype_declaration(p, endToken);
                break;
            case CONSTANT:
                constant_declaration(p, endToken);
                break;
            case FILE:
                file_declaration(p, endToken);
                break;
            case ALIAS:
                alias_declaration(p, endToken);
                break;
            case USE:
                use_clause(p, endToken);
                break;
            default:
                exitLoop = true;
                break;
            }
        }
    }

    /**
     * <dl> subprogram_declarative_part ::=
     *   <dd> { subprogram_declarative_item }
     */
    void subprogram_declarative_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBPROGRAM_DECLARATIVE_PART);
        openNodeScope(node);
        subprogram_declarative_items(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> subprogram_specification ::=
     *   <dd> <b>procedure</b> designator [ ( formal_parameter_list ) ]
     *   <br> | [ <b>pure</b> | <b>impure</b> ] <b>function</b> designator [ ( formal_parameter_list ) ]
     *   <ul>       <b>return</b> type_mark </ul>
     */
    void subprogram_specification(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBPROGRAM_SPECIFICATION);
        openNodeScope(node);
        Token tmpToken = findToken(RETURN, endToken);
        int kind = tokenMgr.getNextTokenKind();
        
        consumeToken(kind);
        new ASTtoken(node, tokenImage[kind]);
        
        if(kind == IMPURE || kind == PURE) {
            if(tokenMgr.getNextTokenKind() != FUNCTION) {
                throw new ParserException(tokenMgr.toNextToken());
            }
            consumeToken(FUNCTION);
            new ASTtoken(node, tokenImage[FUNCTION]);
        }

        Token tmpToken0 = findTokenInBlock(LBRACKET, tmpToken);
        if(tmpToken0 != null)
            tmpToken = tmpToken0;
        
        designator(node, tmpToken);
        
        startBlock();
        if(tokenMgr.getNextTokenKind() == LBRACKET) {
            consumeToken(LBRACKET);
            tmpToken = findTokenInBlock(RBRACKET, endToken);
            formal_parameter_list(node, tmpToken);
            consumeToken(RBRACKET);
        }
        endBlock();
        
        if(kind == FUNCTION || kind == IMPURE || kind == PURE) {
            consumeToken(RETURN);
            type_mark(node, endToken);
        }
        
        closeNodeScope(node);
        
        if(kind == PROCEDURE) {
            SymbolParser.parseSubprogramKind((ASTNode)p, PROCEDURE, this);
        }else {
            SymbolParser.parseSubprogramKind((ASTNode)p, FUNCTION, this);
        }
    }

    /**
     * <dl> subprogram_statement_part ::=
     *   <dd> { sequential_statement }
     */
    void subprogram_statement_part(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBPROGRAM_STATEMENT_PART);
        openNodeScope(node);
        sequential_statements(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> subtype_declaration ::=
     *   <dd> <b>subtype</b> identifier <b>is</b> subtype_indication ;
     */
    void subtype_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBTYPE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(SUBTYPE);
        identifier(node, endToken);
        consumeToken(IS);
        subtype_indication(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseSubtypeKind(node, SUBTYPE, this);
    }

    /**
     * <dl> subtype_indication ::=
     *   <dd> [ <i>resolution_function_</i>name ] type_mark [ constraint ] [ tolerance_aspect ]
     */
    void subtype_indication(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUBTYPE_INDICATION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == identifier
            || tokenMgr.getNextTokenKind(2) == identifier) {
            //name(node, endToken);   //TODO check resolution function name
        }
        
        Token tmpToken = findToken(LBRACKET, endToken);
        if(tmpToken == null)
            tmpToken = findToken(RANGE, endToken);
        if(tmpToken == null)
            tmpToken = findToken(TO, endToken);
        if(tmpToken == null)
            tmpToken = findToken(DOWNTO, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        type_mark(node, tmpToken);
        if(tokenMgr.getNextTokenKind() != TOLERANCE
                && tmpToken != endToken) {
            constraint(node, endToken);
        }
        
        if(tokenMgr.getNextTokenKind() == TOLERANCE) {
            tolerance_aspect(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> suffix ::=
     *   <dd> simple_name
     *   <br> | character_literal
     *   <br> | operator_symbol
     *   <br> | <b>all</b>
     */
    void suffix(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSUFFIX);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == identifier) {
            simple_name(node, endToken);
        }else if(kind == character_literal || kind == ALL) {
            Token token = tokenMgr.toNextToken();
            new ASTtoken(node, token.image);
        }else if(kind == string_literal) {
            operator_symbol(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> target ::=
     *   <dd> name
     *   <br> | aggregate
     */
    void target(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTARGET);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == LBRACKET) {
            aggregate(node, endToken);
        }else {
            name(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> term ::=
     *   <dd> factor { multiplying_operator factor }
     */
    void term(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTERM);
        openNodeScope(node);
        while(true) {
            Token tmpToken = getNextMultiplying_operator(endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            factor(node, tmpToken);
            int kind = tokenMgr.getNextTokenKind();
            if(tokenMgr.getNextToken() == endToken
                   || !(kind == MUL || kind == DIV || kind == MOD || kind == REM)) {
                break;
            }
            consumeToken(kind);
            new ASTtoken(node, tokenImage[kind]);
        }
        
        closeNodeScope(node);
    }

    /**
     * <dl> terminal_aspect ::=
     *   <dd> <i>plus_terminal_</i>name [ <b>to</b> <i>minus_terminal_</i>name ]
     */
    void terminal_aspect(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTERMINAL_ASPECT);
        openNodeScope(node);
        
        Token tmpToken = findToken(TO, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        name(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == TO) {
            consumeToken(TO);
            new ASTtoken(node, tokenImage[TO]);
            name(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <dl> terminal_declaration ::=
     *   <dd> <b>terminal</b> identifier_list : subnature_indication ;
     */
    void terminal_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTERMINAL_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(TERMINAL);
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        subnature_indication(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseOtherKind(node, TERMINAL, this);
    }

    /**
     * <dl> through_aspect ::=
     *   <dd> identifier_list [ tolerance_aspect ] [ := expression ] <b>through</b>
     */
    void through_aspect(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTHROUGH_ASPECT);
        openNodeScope(node);
        endToken = findToken(THROUGH, endToken);
        
        Token tmpToken = findToken(TOLERANCE, endToken);
        if(tmpToken == null)
            tmpToken = findToken(ASSIGN, endToken);
        identifier_list(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == TOLERANCE) {
            tmpToken = findToken(ASSIGN, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            tolerance_aspect(node, tmpToken);
        }
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            expression(node, endToken);
        }
        consumeToken(THROUGH);
        closeNodeScope(node);
    }

    /**
     * <dl> timeout_clause ::=
     *   <dd> <b>for</b> <i>time_or_real_</i>expression
     */
    void timeout_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMEOUT_CLAUSE);
        openNodeScope(node);
        consumeToken(FOR);
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> tolerance_aspect ::=
     *   <dd> <b>tolerance</b> <i>string_</i>expression
     */
    void tolerance_aspect(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTOLERANCE_ASPECT);
        openNodeScope(node);
        consumeToken(TOLERANCE);
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> type_conversion ::=
     *   <dd> type_mark ( expression )
     */
    void type_conversion(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTYPE_CONVERSION);
        openNodeScope(node);
        Token tmpToken = findLastLBracketToken(endToken);
        type_mark(node, tmpToken);
        consumeToken(LBRACKET);
        
        tmpToken = findTokenInBlock(RBRACKET, endToken);
        expression(node, tmpToken);
        consumeToken(RBRACKET);
        closeNodeScope(node);
    }

    /**
     * <dl> type_declaration ::=
     *   <dd> full_type_declaration
     *   <br> | incomplete_type_declaration
     */
    void type_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTYPE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        endToken = tokenMgr.getNextToken(endToken);
        if(endToken == null) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        if(findToken(IS, endToken) != null) {
            full_type_declaration(node, endToken);
        }else {
            incomplete_type_declaration(node, endToken);
        }
        closeNodeScope(node);
        SymbolParser.parseTypeKind(node, TYPE, this);
    }

    /**
     * <dl> type_definition ::=
     *   <dd> scalar_type_definition
     *   <br> | composite_type_definition
     *   <br> | access_type_definition
     *   <br> | file_type_definition
     */
    void type_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTYPE_DEFINITION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == LBRACKET || kind == RANGE) {
            scalar_type_definition(node, endToken);
        }else if(kind == ARRAY || kind == RECORD) {
            composite_type_definition(node, endToken);
        }else if(kind == ACCESS) {
            access_type_definition(node, endToken);
        }else if(kind == FILE) {
            file_type_definition(node, endToken);
        }else {
            throw new ParserException(tokenMgr.toNextToken());
        }
        closeNodeScope(node);
    }

    /**
     * <dl> type_mark ::=
     *   <dd> <i>type_</i>name
     *   <br> | <i>subtype_</i>name
     */
    void type_mark(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTYPE_MARK);
        openNodeScope(node);
        name(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> unconstrained_array_definition ::=
     *   <dd> <b>array</b> ( index_subtype_definition { , index_subtype_definition } )
     *   <ul> <b>of</b> <i>element_</i>subtype_indication </ul>
     */
    void unconstrained_array_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUNCONSTRAINED_ARRAY_DEFINITION);
        openNodeScope(node);
        consumeToken(ARRAY);
        consumeToken(LBRACKET);
        
        Token rbracketToken = findTokenInBlock(RBRACKET, endToken);
        if(rbracketToken == null) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, rbracketToken);
            if(tmpToken == null)
                tmpToken = rbracketToken;
            index_subtype_definition(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RBRACKET);        
        consumeToken(OF);
        new ASTtoken(node, tokenImage[OF]);
        subtype_indication(node, endToken);
        closeNodeScope(node);
    }

    /**
     * <dl> unconstrained_nature_definition ::=
     *   <dd> <b>array</b> ( index_subtype_definition { , index_subtype_definition } )
     *   <ul> <b>of</b> subnature_indication </ul>
     */
    void unconstrained_nature_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUNCONSTRAINED_NATURE_DEFINITION);
        openNodeScope(node);
        consumeToken(ARRAY);
        consumeToken(LBRACKET);
        
        Token rbracketToken = findTokenInBlock(RBRACKET, endToken);
        if(rbracketToken == null) {
            throw new ParserException(tokenMgr.toNextToken());
        }
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, rbracketToken);
            if(tmpToken == null)
                tmpToken = rbracketToken;
            index_subtype_definition(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RBRACKET);
        consumeToken(OF);
        new ASTtoken(node, tokenImage[OF]);
        subnature_indication(node, endToken);
        closeNodeScope(node);
    }
    
    private String[] getSelectedNames(ASTNode node) {
        String[] ret = new String[3];
        if(node.getId() != VhdlASTConstants.ASTSELECTED_NAME) {
            return null;
        }
        
        String libName = "", pkgName = "", symbolName = "";
        Token token = node.first_token;
        while(!token.image.equals(".")) {
            libName += token.image;
            token = token.next;
        }
        
        token = token.next;
        while(!token.image.equals(".")) {
            pkgName += token.image;
            if(token == node.last_token) {
                break;
            }
            token = token.next;
            if(token == null) {
                MyDebug.printFileLine("error");
            }
        }
        
        if(token != node.last_token) {
            token = token.next;
            while(token != node.last_token) {
                symbolName += token.image;
                token = token.next;
            }
            symbolName += token.image;
        }
        
        ret[0] = libName;
        ret[1] = pkgName;
        ret[2] = symbolName;
        return ret;
    }
    
    /**
     * add all children table to use table 
     */
    private void addAllTable(ASTNode node, SymbolTable symTab) {
        Symbol[] allSyms = (Symbol[])symTab.getAllSymbols();
        boolean subTableExist = false;
        if(allSyms != null) {
            String tabName = symTab.getTableName();
            for(int i = 0; i < allSyms.length; i++) {
                String tabName1 = tabName + "#" + allSyms[i].getName();
                if(!SymbolTable.isTableExist(node.getSymbolTable(), tabName1))
                    continue;
                SymbolTable symTab1 = new LibSymbolTable(symTab, allSyms[i].getName());
                addAllTable(node, symTab1);
                subTableExist = true;
            }
        }
        
        if(!subTableExist) {
            extSymbolTable.add(symTab);
        }
    }
    
    /**
     * add work.all table to use table
     */
    private void addWorkAll(ASTNode node) {
        HashMap<String, VhdlArrayList<Symbol>> symbolMap = libMgr.getSymbolMap();
        Set<String> keys = symbolMap.keySet();
        Iterator<String> keyIter = keys.iterator();
        while (keyIter.hasNext()) {
           String tabName = keyIter.next();
           SymbolTable symTab = new LibSymbolTable(null, tabName);
           addAllTable(node, symTab);
        }
    }

    /**
     * <dl> use_clause ::=
     *   <dd> <b>use</b> selected_name { , selected_name } ;
     */
    void use_clause(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUSE_CLAUSE);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        consumeToken(USE);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            selected_name(node, tmpToken);
            if(!parseSymbol) {
                String[] selNames = getSelectedNames((ASTNode)node.getChild(
                                                node.getChildrenNum() - 1));
                if(selNames[0].equalsIgnoreCase("work")) {
                    if(selNames[1].equalsIgnoreCase("all")) {
                        // work.all
                        addWorkAll(node);
                        if(tokenMgr.getNextTokenKind() != COMMA) {
                            break;
                        }
                        consumeToken(COMMA);
                        continue;
                    }else {
                        selNames[0] = libMgr.findWorkLibrary(selNames[1]);
                    }
                }
                
                SymbolTable symTab = new LibSymbolTable(null, selNames[0]);
                if(!selNames[2].isEmpty()) {
                    symTab = new LibSymbolTable(symTab, selNames[1]);
                    if(selNames[2].equalsIgnoreCase("all")) {
                        // get all symbols in first two segments
                        addAllTable(node, symTab);
                        
                    }else {
                        // three segments, specified symbol
                        symTab = new LibSymbolTable(symTab, selNames[2]);
                        extSymbolTable.add(symTab);
                    }
                }else {
                    // only two segments
                    if(selNames[1].equalsIgnoreCase("all")) {
                        // get all symbols in first segments
                        addAllTable(node, symTab);
                    }else {
                        // specified symbol
                        String tabName = libMgr.getTableName(selNames[0], selNames[1]);
                        StringTokenizer tkn = new StringTokenizer(tabName, "#");
                        symTab = null;
                        while(tkn.hasMoreTokens()) {
                            String seg = tkn.nextToken();
                            symTab = new LibSymbolTable(symTab, seg);
                        }
                        extSymbolTable.add(symTab);
                    }
                }
            }
            
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> variable_assignment_statement ::=
     *   <dd> [ label : ] target := expression ;
     */
    void variable_assignment_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTVARIABLE_ASSIGNMENT_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
        }
        
        tmpToken = findToken(ASSIGN, endToken);
        target(node, tmpToken);
        consumeToken(ASSIGN);
        expression(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> variable_declaration ::=
     *   <dd> [ <b>shared</b> ] <b>variable</b> identifier_list 
     *        : subtype_indication [ := expression ] ;
     */
    void variable_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTVARIABLE_DECLARATION);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        if(tokenMgr.getNextTokenKind() == SHARED) {
            consumeToken(SHARED);
            new ASTtoken(node, tokenImage[SHARED]);
        }
        consumeToken(VARIABLE);
        
        Token tmpToken = findToken(COLON, endToken);
        identifier_list(node, tmpToken);
        consumeToken(COLON);
        
        tmpToken = findToken(ASSIGN, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        subtype_indication(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            consumeToken(ASSIGN);
            expression(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, VARIABLE, this);
    }

    /**
     * <dl> wait_statement ::=
     *   <dd> [ label : ] <b>wait</b> [ sensitivity_clause ] 
     *          [ condition_clause ] [ timeout_clause ] ;
     */
    void wait_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTWAIT_STATEMENT);
        openNodeScope(node);
        endToken = findToken(SEMICOLON, endToken);
        
        Token tmpToken = endToken;
        if(tokenMgr.getNextTokenKind(2) == COLON) {
            tmpToken = findToken(COLON, endToken);
            identifier(node, tmpToken);   // label
            consumeToken(COLON);
        }
        
        consumeToken(WAIT);
        if(tokenMgr.getNextTokenKind() == ON) {
            tmpToken = findToken(UNTIL, endToken);
            if(tmpToken == null)
                tmpToken = findToken(FOR, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            sensitivity_clause(node, tmpToken);
        }
        if(tokenMgr.getNextTokenKind() == UNTIL) {
            tmpToken = findToken(FOR, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            condition_clause(node, tmpToken);
        }
        if(tokenMgr.getNextTokenKind() == FOR) {
            timeout_clause(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * <dl> waveform ::=
     *   <dd> waveform_element { , waveform_element }
     *   <br> | <b>unaffected</b>
     */
    void waveform(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTWAVEFORM);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == UNAFFECTED) {
            consumeToken(UNAFFECTED);
            new ASTtoken(node, tokenImage[UNAFFECTED]);
        }else {
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                waveform_element(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
        }
        closeNodeScope(node);
    }

    /**
     * <dl> waveform_element ::=
     *   <dd> <i>value_</i>expression [ <b>after</b> <i>time_</i>expression ]
     *   <br> | <b>null</b> [ <b>after</b> <i>time_</i>expression ]
     */
    void waveform_element(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTWAVEFORM_ELEMENT);
        openNodeScope(node);
        
        if(tokenMgr.getNextTokenKind() == NULL) {
            consumeToken(NULL);
            new ASTtoken(node, tokenImage[NULL]);
        }else {
            Token tmpToken = findToken(AFTER, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            expression(node, tmpToken);
        }
        if(tokenMgr.getNextTokenKind() == AFTER) {
            consumeToken(AFTER);
            new ASTtoken(node, tokenImage[AFTER]);
            expression(node, endToken);
        }
        closeNodeScope(node);
    }
    
    public ArrayList<ASTNode> getLocalUnits() {
        return localUnits;
    }

    @Override
    public CommentBlock[] getComment() {
        if(tokenMgr == null) {
            MyDebug.printFileLine("you must parse the file by call parse() firstly");
            return null;
        }
        return tokenMgr.getComment();
    }

    @Override
    public IASTNode getRoot() {
        return designFile;
    }
    
    protected Symbol getNamesSymbol(SymbolTable curTab, String[] names) {
        Symbol sym = null;
        if(curTab == null || names == null || names.length == 0) {
            MyDebug.printFileLine("VhdlParser.getNamesSymbol: null pointer");
            return null;
        }

        
        for(int i = 0; i < names.length; i++)
        {
            if(curTab == null) {
                return null;
            }
            
            sym = (Symbol)curTab.getSymbol(names[i]);
            if(sym == null) {
                return null;
            }
            
            if(i < names.length - 1) {
                if((curTab = curTab.getTableOfSymbol(sym.type)) == null) {
                    return null;
                }
                curTab = curTab.getChildTable(sym.type);
            }
        }
            
        return sym;
    }

    @Override
    public ISymbol getSymbol(IASTNode node, String name) {
        if(node == null) {
            MyDebug.printFileLine("null parameter");
            return null;
        }
        if(tokenMgr == null) {
            MyDebug.printFileLine("you must parse the file by calling parse() firstly");
            return null;
        }
        
        if(((ASTNode)node).getSymbolTable() == null) {
            //MyDebug.printFileLine("symbol table is null!");
            return null;
        }
        
        ISymbol sym = ((ASTNode)node).getSymbolTable().getSymbol(name);
        if(sym != null) {
            return sym;
        }
        
        for(int i = 0; i < extSymbolTable.size(); i++) {
            if((sym = extSymbolTable.get(i).getSymbol(name)) != null) {
                return sym;
            }
        }
        return null;
    }
    
    @Override
    public ISymbol getSymbol(IASTNode node, String[] names)
    {
        if(node == null || names == null) {
            MyDebug.printFileLine("null parameter");
            return null;
        }
        if(tokenMgr == null) {
            MyDebug.printFileLine("you must parse the file by calling parse() firstly");
            return null;
        }
        
        Symbol ret = getNamesSymbol(((ASTNode)node).getSymbolTable(), names);
        if(ret != null)
            return ret;
        
        for(int i = 0; i < extSymbolTable.size(); i++) {
            if((ret = getNamesSymbol(extSymbolTable.get(i), names)) != null) {
                return ret;
            }
        }
        return null;
    }
    
    @Override
    public ISymbolTable getTableOfSymbol(IASTNode node, String name)
    {
        SymbolTable ret = ((ASTNode)node).getSymbolTable().getTableOfSymbol(name);
        if(ret != null) {
            return ret;
        }
        
        for(int i = 0; i < extSymbolTable.size(); i++) {
            ret = extSymbolTable.get(i).getTableOfSymbol(name);
            if(ret != null) {
                return ret;
            }
        }
        return null;
    }

    @Override
    public IASTNode parse(String path) throws ParserException {
        try {
            BufferedReader stream = new BufferedReader(new FileReader(path));
            tokenMgr = new VhdlTokenManager(stream, parseSymbol);
            return design_file();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public IASTNode parse(Reader reader) throws ParserException {
        BufferedReader stream = new BufferedReader(reader);
        tokenMgr = new VhdlTokenManager(stream, parseSymbol);
        return design_file();
    }
}

