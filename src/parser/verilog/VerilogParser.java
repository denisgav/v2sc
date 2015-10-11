package parser.verilog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;

import common.MyDebug;

import parser.CommentBlock;
import parser.IASTNode;
import parser.IParser;
import parser.ISymbol;
import parser.ISymbolTable;
import parser.ParserException;
import parser.Token;

public class VerilogParser implements IParser, VerilogTokenConstants, 
          VerilogASTConstants, SystemConstants, CompilerDirectives
{
    protected VerilogTokenManager tokenMgr = null;
    protected ASTNode curNode = null;       // current parsing node
    protected ASTNode lastNode = null;      // last parsed node
    protected ASTNode sourceText = null;   // source text node
    
    protected VerilogArrayList<SymbolTable> extSymbolTable = new VerilogArrayList<SymbolTable>();
    protected SymbolTable curSymbolTable = null;
    protected LibraryManager libMgr = LibraryManager.getInstance();
    
    protected HashMap<String, ASTNode> defineMap = null;
    
    /**
     *  true -- just only parse symbols(if exist)<br>
     *  false -- parse all AST
     */
    protected boolean parseSymbol = false;
    
    /**
     * constructor, file path version
     */
    public VerilogParser(boolean parseSymbol) {
        this.parseSymbol = parseSymbol;
        curSymbolTable = new LocalSymbolTable(null, "VerilogFile");
    }
    
    /**
     * token which has its own symbol table call this function to start<br>
     * use pair with endBlock
     */
    void startBlock() {
        //TODO:add here
        SymbolTable newTab = new LocalSymbolTable(curSymbolTable, curNode.getName());
        curSymbolTable = newTab;
    }
    
    /**
     * token which has its own symbol table call this function to end<br>
     * use pair with startBlock
     */
    void endBlock() {
      //TODO:add here
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
     * find any one of token of kinds
     */
    Token findTokenArray(int[] kinds, Token endToken) throws ParserException {
        Token ret = null;
        for(int i = 0; i < kinds.length; i++) {
            if((ret = findTokenInBlock(kinds[i], endToken)) != null) {
                break;
            }
        }
        return ret;
    }
    
    /**
     * find any one of token of images
     */
    Token findTokenArray(String[] images, Token endToken) throws ParserException {

        for(int i = 0; i < images.length; i++) {
            Token tmpToken = tokenMgr.getNextToken();
            while(tmpToken != null && tmpToken != endToken) {
                if(tmpToken.image.equals(images[i])) {
                    return tmpToken;
                }
                tmpToken = tokenMgr.getNextToken(tmpToken);
            }
        }
        return null;
    }
    
    boolean is_unary_operator() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ADD || kind == SUB || kind == LOGIC_NEG || kind == BIT_NEG
                || kind == BIT_AND || kind == BIT_OR || kind == BIT_XOR
                || kind == BIT_XORN || kind == BIT_NXOR || kind == REDUCE_NAND
                || kind == REDUCE_NOR) {
            return true;
        }
        return false;
    }
    
    boolean is_polarity_operator() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ADD || kind == SUB) {
            return true;
        }
        return false;
    }
   
    boolean is_binary_operator() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == ADD || kind == SUB || kind == MUL || kind == DIV
                || kind == MOD || kind == LOGIC_EQ || kind == LOGIC_NEQ
                || kind == CASE_EQ || kind == CASE_NEQ || kind == LOGIC_AND
                || kind == LOGIC_OR || kind == LO || kind == LE || kind == GT
                || kind == GE || kind == BIT_AND || kind == BIT_OR || kind == BIT_XOR
                || kind == BIT_XORN || kind == BIT_NXOR || kind == SHIFT_LEFT
                || kind == SHIFT_RIGHT) {
            return true;
        }
        return false;
    }
    
    Token find_binary_operator(Token endToken) throws ParserException {
        Token oldToken = tokenMgr.getCurrentToken();
        int temp = tokenMgr.getNextTokenKind();
        if(temp != LPARENTHESIS && temp != LBRACE) {
            tokenMgr.toNextToken();
        }
        final int[] kinds = {ADD, SUB, MUL, DIV, MOD, LOGIC_EQ, LOGIC_NEQ,
                CASE_EQ, CASE_NEQ, LOGIC_AND, LOGIC_OR, LO, LE, GT, GE,
                BIT_AND, BIT_OR, BIT_XOR, BIT_XORN, BIT_NXOR, SHIFT_LEFT,
                SHIFT_RIGHT};
        Token ret = findTokenArray(kinds, endToken);
        tokenMgr.setCurrentToken(oldToken);
        return ret;
    }
    
    Token find_gatetype(Token endToken) throws ParserException {
        final int[] kinds = {BUF, BUFIF0, BUFIF1, NOT, NOTIF0, NOTIF1, PULLDOWN,
                PULLUP, NMOS, RNMOS, PMOS, RPMOS, CMOS, RCMOS, TRAN, RTRAN, TRANIF0,
                RTRANIF0, TRANIF1, RTRANIF1};
        return findTokenArray(kinds, endToken);
    }
    
    Token find_nettype(Token endToken) throws ParserException {
        final int[] kinds = {WIRE, TRI, TRI1, SUPPLY0, WAND, TRIAND, TRI0,
                SUPPLY1, WOR, TRIOR, TRIREG};
        return findTokenArray(kinds, endToken);
    }
    
    Token find_edge(Token endToken) throws ParserException {
        Token ret = find_edge_symbol(endToken);
        if(ret != null)
            return ret;
        final int[] kinds = {EDGE_01, EDGE_0x, EDGE_10, EDGE_1x, EDGE_x0, EDGE_x1};
        return findTokenArray(kinds, endToken);
    }
    
    Token find_delay_or_event(Token endToken) throws ParserException {
        final int[] kinds = {AT, PARA};
        return findTokenArray(kinds, endToken);
    }
    
    Token find_edge_symbol(Token endToken) throws ParserException {
        final String[] syms = {"r", "R", "f", "F", "p", "P", "n", "N", "*"};
        return findTokenArray(syms, endToken);
    }
    
    Token find_level_symbol(Token endToken) throws ParserException {
        final String[] syms = {"0", "1", "x", "X", "?", "b", "B"};
        return findTokenArray(syms, endToken);
    }
    
    Token find_output_symbol(Token endToken) throws ParserException {
        final String[] syms = {"0", "1", "x", "X"};
        return findTokenArray(syms, endToken);
    }
    
    boolean is_level_symbol_array() throws ParserException {
        final String regex = "[01xX?bB]+";
        Token token = tokenMgr.getNextToken();
        if(token == null)
            return false;
        return token.image.matches(regex);
    }

    boolean is_strength0() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == SUPPLY0 || kind == STRONG0 || kind == PULL0 || kind == WEAK0
                || kind == HIGHZ0) {
            return true;
        }
        return false;
    }
    
    boolean is_strength1() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == SUPPLY1 || kind == STRONG1 || kind == PULL1 || kind == WEAK1
                || kind == HIGHZ1) {
            return true;
        }
        return false;
    }
    
    boolean is_range_or_type() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == LSQUARE_BRACKET || kind == INTEGER || kind == REAL) {
            return true;
        }
        return false;
    }
    
    boolean is_tf_declaration() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == INTEGER || kind == REAL || kind == PARAMETER || kind == INPUT
                || kind == OUTPUT || kind == INOUT || kind == REG 
                || kind == TIME) {
            return true;
        }
        return false;
    }
    
    boolean is_expandrange() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == SCALARED || kind == VECTORED || kind == LSQUARE_BRACKET) {
            return true;
        }
        return false;
    }
    
    boolean is_delay() throws ParserException {
        return (tokenMgr.getNextTokenKind() == PARA);
    }
    
    boolean is_block_declaration() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == PARAMETER || kind == REG || kind == INTEGER || kind == TIME 
                || kind == EVENT || kind == REAL) {
            return true;
        }
        return false;
    }
    
    boolean is_udp_declaration() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == OUTPUT || kind == REG || kind == INPUT) {
            return true;
        }
        return false;
    }
    
    boolean is_timing_check_event_control() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == POSEDGE || kind == NEGEDGE || kind == EDGE) {
            return true;
        }
        return false;
    }
    
    boolean is_description() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == PRIMITIVE || kind == MODULE || kind == MACROMODULE) {
            return true;
        }
        return false;
    }
    
    boolean is_edge_identifier() throws ParserException {
        int kind = tokenMgr.getNextTokenKind();
        if(kind == POSEDGE || kind == NEGEDGE) {
            return true;
        }
        return false;
    }
    
    boolean is_compiler_directives() throws ParserException {
        Token token = tokenMgr.getNextToken();
        if(token != null && token.image.startsWith("`")) {
            return true;
        }
        return false;
    }
    
    boolean is_ifdef() throws ParserException {
        Token token = tokenMgr.getNextToken();
        if(token != null && token.image.equals(cdStrings[CD_IFDEF])) {
            return true;
        }
        return false;
    }
    
    /**
     * check whether specified token is behind base token
     */
    boolean checkLateComming(Token token, Token base) throws ParserException {
        if(base == null) { return false; }
        if(token == null) { return true; }
        if(token == base) { return true; }
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
            
            Token tmp1 = null;
            switch(token.kind)
            {
            case LSQUARE_BRACKET:
                tmp1 = findTokenInBlock(nextToken, RSQUARE_BRACKET, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case LBRACE:
                tmp1 = findTokenInBlock(nextToken, RBRACE, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case LPARENTHESIS:
                tmp1 = findTokenInBlock(nextToken, RPARENTHESIS, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case MODULE:
            case MACROMODULE:
                tmp1 = findTokenInBlock(nextToken, ENDMODULE, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case PRIMITIVE:
                tmp1 = findTokenInBlock(nextToken, ENDPRIMITIVE, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case TABLE:
                tmp1 = findTokenInBlock(nextToken, ENDTABLE, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case TASK:
                tmp1 = findTokenInBlock(nextToken, ENDTASK, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case FUNCTION:
                tmp1 = findTokenInBlock(nextToken, ENDFUNCTION, to);
                tmp1 = findTokenInBlock(nextToken, ENDFUNCTION, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case CASE:
            case CASEX:
            case CASEZ:
                tmp1 = findTokenInBlock(nextToken, ENDCASE, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case BEGIN:
                tmp1 = findTokenInBlock(nextToken, END, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case FORK:
                tmp1 = findTokenInBlock(nextToken, JOIN, to);
                nextToken = tokenMgr.getNextToken(tmp1); // ignore block
                break;
                
            case SPECIFY:
                tmp1 = findTokenInBlock(nextToken, ENDSPECIFY, to);
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
            } else if(token.kind == kind) {
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
     * always_statement <br>
     *     ::= <b>always</b>  statement  
     */
    void always_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTALWAYS_STATEMENT);
        openNodeScope(node);
        consumeToken(ALWAYS);
        statement(node, endToken);
        closeNodeScope(node);
    }

    /**
     * assignment <br>
     *     ::=  lvalue  =  expression  
     */
    void assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTASSIGNMENT);
        openNodeScope(node);
        Token tmpToken = findToken(EQ, endToken);
        lvalue(node, tmpToken);
        consumeToken(EQ);
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * base  is one of the following tokens: <br>
     *     'b 'B 'o 'O 'd 'D 'h 'H 
     */
//    void base(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTBASE);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * binary_operator  is one of the following tokens: <br>
     *     + - * / % == != === !== && || < <= > >= & | ^ ^~ >> << 
     */
    void binary_operator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBINARY_OPERATOR);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }
    
    /**
     * block_declaration <br>
     *     ::=  parameter_declaration  <br>
     *     ||=  reg_declaration  <br>
     *     ||=  integer_declaration  <br>
     *     ||=  real_declaration  <br>
     *     ||=  time_declaration  <br>
     *     ||=  event_declaration  
     */
    void block_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_DECLARATION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        switch(kind)
        {
        case PARAMETER:
            parameter_declaration(node, endToken);
        case REG:
            reg_declaration(node, endToken);
            break;
        case INTEGER:
            integer_declaration(node, endToken);
            break;
        case TIME:
            time_declaration(node, endToken);
            break;
        case EVENT:
            event_declaration(node, endToken);
            break;
        default:
            real_declaration(node, endToken);
            break;
        }
        closeNodeScope(node);
    }
    
    /**
     * blocking_assignment <br>
     *     ::=  lvalue  =  expression  <br>
     *     ||=  lvalue  =  delay_or_event_control   expression 
     */
    void blocking_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCKING_ASSIGNMENT);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(EQ, endToken);
        lvalue(node, tmpToken);
        consumeToken(EQ);

        tmpToken = find_delay_or_event(endToken);
        if(tmpToken != null) {
            delay_or_event_control(node, tmpToken); //TODO: endToken ?
            expression(node, endToken);
        }else {
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * case_item <br>
     *     ::=  expression  {, expression } :  statement_or_null  <br>
     *     ||= <b>default</b> :  statement_or_null  <br>
     *     ||= <b>default</b>  statement_or_null  
     */
    void case_item(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCASE_ITEM);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == DEFAULT) {
            new ASTtoken(node, tokenImage[DEFAULT]);
            consumeToken(DEFAULT);
            if(tokenMgr.getNextTokenKind() == COLON) {
                new ASTtoken(node, tokenImage[COLON]);
                consumeToken(COLON);
            }
            statement_or_null(node, endToken);
        }else {
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                Token tmpToken1 = findTokenInBlock(COLON, endToken);
                if(tmpToken == null || checkLateComming(tmpToken, tmpToken1)) {
                    tmpToken = tmpToken1;
                }
                expression(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
            new ASTtoken(node, tokenImage[COLON]);
            consumeToken(COLON);
            statement_or_null(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * charge_strength <br>
     *     ::= ( <b>small</b> ) <br>
     *     ||= ( <b>medium</b> ) <br>
     *     ||= ( <b>large</b> ) 
     */
    void charge_strength(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCHARGE_STRENGTH);
        openNodeScope(node);
        consumeToken(LPARENTHESIS);
        Token token = tokenMgr.getNextToken();
        if(token == null || !(token.kind == SMALL || token.kind == MEDIUM 
                || token.kind == LARGE)) {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        new ASTtoken(node, token.image);
        consumeToken(token.kind);
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * combinational_entry <br>
     *     ::=  level_input_list  :  output_symbol  ; 
     */
    void combinational_entry(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMBINATIONAL_ENTRY);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(COLON, endToken);
        level_input_list(node, tmpToken);
        consumeToken(COLON);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        output_symbol(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * concatenation <br>
     *     ::= <b>{</b>  expression  {, expression } <b>}</b> 
     */
    void concatenation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONCATENATION);
        openNodeScope(node);
        consumeToken(LBRACE);
        
        endToken = findTokenInBlock(RBRACE, endToken);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            expression(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RBRACE);
        closeNodeScope(node);
    }

    /**
     * conditional_port_expression <br>
     *     ::=  port_reference  <br>
     *     ||=  unary_operator  port_reference  <br>
     *     ||=  port_reference  binary_operator  port_reference  
     */
    void conditional_port_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONDITIONAL_PORT_EXPRESSION);
        openNodeScope(node);
        if(is_unary_operator()) {
            unary_operator(node, endToken);
            port_reference(node, endToken);
        }else {
            port_reference(node, endToken);
            if(is_binary_operator()) {
                binary_operator(node, endToken);
                port_reference(node, endToken);
            }
        }
        closeNodeScope(node);
    }

    /**
     * constant_expression <br>
     *     ::= expression  
     */
    void constant_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONSTANT_EXPRESSION);
        openNodeScope(node);
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * continuous_assign <br>
     *     ::= <b>assign</b> [ drive_strength ] [ delay ]  list_of_assignments  ; <br>
     *     ||=  nettype  [ drive_strength ] [ expandrange ] [ delay ]  list_of_assignments  ; 
     */
    void continuous_assign(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONTINUOUS_ASSIGN);
        openNodeScope(node);
        Token tmpToken = null;
        endToken = findTokenInBlock(SEMICOLON, endToken);
        if(tokenMgr.getNextTokenKind() == ASSIGN) {
            new ASTtoken(node, tokenImage[ASSIGN]);
            consumeToken(ASSIGN);
        }else {
            nettype(node, endToken);
        }
        
        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            drive_strength(node, tmpToken);
        }
        
        if(is_expandrange()) {
            expandrange(node, endToken);
        }
        
        if(is_delay()) {
            delay(node, endToken);
        }
        list_of_assignments(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * controlled_timing_check_event <br>
     *     ::=  timing_check_event_control   specify_terminal_descriptor  <br>
     *         [&&&  timing_check_condition ] 
     */
    void controlled_timing_check_event(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONTROLLED_TIMING_CHECK_EVENT);
        openNodeScope(node);
        timing_check_event_control(node, endToken);
        specify_terminal_descriptor(node, endToken);
        if(tokenMgr.getNextTokenKind() == TRI_AND) {
            timing_check_condition(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * data_source_expression <br>
     *     Any expression, including constants <b>and</b> lists. Its width must be one bit <b>or</b> <br>
     *     equal to the destination's width. If the destination is a list, the data <br>
     *     source must be as wide as the sum of the bits of the members. 
     */
    void data_source_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDATA_SOURCE_EXPRESSION);
        openNodeScope(node);
        //TODO:fix here
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * decimal_number <br>
     *     ::= A number containing a set of any of the following characters, optionally preceded by + <b>or</b> - <br>
     *          0123456789_ 
     */
//    void decimal_number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTDECIMAL_NUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * delay <br>
     *     ::= #  number  <br>
     *     ||= #  IDENTIFIER  <br>
     *     ||= # ( mintypmax_expression  [, mintypmax_expression ] [, mintypmax_expression ]) 
     */
    void delay(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY);
        openNodeScope(node);
        consumeToken(PARA);
        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            consumeToken(LPARENTHESIS);
            Token tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            mintypmax_expression(node, tmpToken);
            if(tokenMgr.getNextTokenKind() == COMMA) {
                mintypmax_expression(node, tmpToken);
            }
            if(tokenMgr.getNextTokenKind() == COMMA) {
                mintypmax_expression(node, tmpToken);
            }
            consumeToken(RPARENTHESIS);
        }else {
            tokenMgr.toNextToken(); // number or IDENTIFIER
        }
        closeNodeScope(node);
    }

    /**
     * delay_control <br>
     *     ::= #  number  <br>
     *     ||= #  IDENTIFIER  <br>
     *     ||= # (  mintypmax_expression  ) 
     */
    void delay_control(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY_CONTROL);
        openNodeScope(node);
        consumeToken(PARA);
        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            consumeToken(LPARENTHESIS);
            Token tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            mintypmax_expression(node, tmpToken);
            consumeToken(RPARENTHESIS);
        }else {
            tokenMgr.toNextToken(); // number or IDENTIFIER
        }
        closeNodeScope(node);
    }

    /**
     * delay_or_event_control <br>
     *     ::=  delay_control  <br>
     *     ||=  event_control  <br>
     *     ||= <b>repeat</b> (  expression  )  event_control  
     */
    void delay_or_event_control(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY_OR_EVENT_CONTROL);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == PARA) {
            delay_control(node, endToken);
        }else if(kind == REPEAT) {
            new ASTtoken(node, tokenImage[REPEAT]);
            consumeToken(REPEAT);
            consumeToken(LPARENTHESIS);
            Token tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            expression(node, tmpToken);
            consumeToken(RPARENTHESIS);
            event_control(node, endToken);
        }else {
            event_control(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * description <br>
     *     ::=  module  <br>
     *     ||=  udp  
     */
    void description(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDESCRIPTION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == PRIMITIVE) {
            udp(node, endToken);
        }else {
            module(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * drive_strength <br>
     *     ::= (  strength0  ,  strength1  ) <br>
     *     ||= (  strength1  ,  strength0  ) 
     */
    void drive_strength(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDRIVE_STRENGTH);
        openNodeScope(node);
        consumeToken(LPARENTHESIS);
        endToken = findTokenInBlock(RPARENTHESIS, endToken);
        Token tmpToken = findTokenInBlock(COMMA, endToken);
        if(is_strength0()) {
            strength0(node, tmpToken);
            strength1(node, endToken);
        }else {
            strength1(node, tmpToken);
            strength0(node, endToken);
        }
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * <b>edge</b> <br>
     *     ::= (  level_symbol   level_symbol  ) <br>
     *     ||=  edge_symbol  
     */
    void edge(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            new ASTtoken(node, tokenImage[LPARENTHESIS]);
            consumeToken(LPARENTHESIS);
            new ASTtoken(node, tokenMgr.getNextToken().image);
            tokenMgr.toNextToken();
            consumeToken(RPARENTHESIS);
        }else {
            new ASTtoken(node, tokenMgr.getNextToken().image);
            tokenMgr.toNextToken();
        }
        closeNodeScope(node);
    }

    /**
     * edge_control_specifier <br>
     *     ::= <b>edge</b> <b>[</b>  edge_descriptor {, edge_descriptor } <b>]</b> 
     */
    void edge_control_specifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_CONTROL_SPECIFIER);
        openNodeScope(node);
        consumeToken(EDGE);
        consumeToken(LSQUARE_BRACKET);
        endToken = findTokenInBlock(RSQUARE_BRACKET, endToken);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            edge_descriptor(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RSQUARE_BRACKET);
        closeNodeScope(node);
    }

    /**
     * edge_descriptor <br>
     *     ::= 01 <br>
     *     ||= 10 <br>
     *     ||= 0x <br>
     *     ||= x1 <br>
     *     ||= 1x <br>
     *     ||= x0 
     */
    void edge_descriptor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_DESCRIPTOR);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * edge_identifier <br>
     *     ::= <b>posedge</b> <br>
     *     ||= <b>negedge</b> 
     */
    void edge_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_IDENTIFIER);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }
    
    /**
     * edge_input_list <br>
     *     ::= { level_symbol }  edge  { level_symbol } 
     */
    void edge_input_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_INPUT_LIST);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        edge(node, endToken);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * edge_sensitive_path_declaration <br>
     *     ::= [if ( expression )] ([ edge_identifier ] <br>
     *          specify_input_terminal_descriptor  => <br>
     *         ( specify_output_terminal_descriptor  [ polarity_operator ] <br>
     *         :  data_source_expression )) =  path_delay_value ; <br>
     *     ||= [if ( expression )] ([ edge_identifier ] <br>
     *          specify_input_terminal_descriptor  *> <br>
     *         ( list_of_path_outputs  [ polarity_operator ] <br>
     *         :  data_source_expression )) = path_delay_value ; 
     */
    void edge_sensitive_path_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_SENSITIVE_PATH_DECLARATION);
        openNodeScope(node);
        Token tmpToken = null, tmpToken1 = null;
        endToken = findTokenInBlock(SEMICOLON, endToken);
        if(tokenMgr.getNextTokenKind() == IF) {
            consumeToken(IF);
            consumeToken(LPARENTHESIS);
            tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            expression(node, tmpToken);
            consumeToken(RPARENTHESIS);
        }
        
        consumeToken(LPARENTHESIS);
        tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
        if(is_edge_identifier()) {
            edge_identifier(node, tmpToken);
        }
        
        tmpToken1 = findTokenInBlock(EQ_ARROW, endToken);
        if(tmpToken1 != null) {
            specify_input_terminal_descriptor(node, tmpToken1);
            consumeToken(EQ_ARROW);
            consumeToken(LPARENTHESIS);
            tmpToken = findTokenInBlock(COLON, endToken);
            specify_output_terminal_descriptor(node, tmpToken);
        }else {
            tmpToken1 = findTokenInBlock(STAR_ARROW, endToken);
            specify_input_terminal_descriptor(node, tmpToken1);
            consumeToken(STAR_ARROW);
            consumeToken(LPARENTHESIS);
            tmpToken = findTokenInBlock(COLON, endToken);
            list_of_path_outputs(node, tmpToken);
        }
        
        if(is_polarity_operator()) {
            polarity_operator(node, tmpToken);
        }
        tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
        data_source_expression(node, tmpToken);
        consumeToken(RPARENTHESIS);
        consumeToken(RPARENTHESIS);

        consumeToken(EQ);
        
        path_delay_value(node, endToken);
        
        closeNodeScope(node);
    }

    /**
     * edge_symbol  is one of the following characters: <br>
     *     r R f F p P n N * 
     */
//    void edge_symbol(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTEDGE_SYMBOL);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }
    
    /**
     * event_control <br>
     *     ::= @  IDENTIFIER  <br>
     *     ||= @ (  event_expression  ) 
     */
    void event_control(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEVENT_CONTROL);
        openNodeScope(node);
        consumeToken(AT);
        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            consumeToken(LPARENTHESIS);
            endToken = findTokenInBlock(RPARENTHESIS, endToken);
            event_expression(node, endToken);
            consumeToken(RPARENTHESIS);
        }else {
            new ASTtoken(node, tokenMgr.getNextToken().image);  // IDENTIFIER
            tokenMgr.toNextToken();
        }
        closeNodeScope(node);
    }

    /**
     * event_declaration <br>
     *     ::= <b>event</b>  name_of_event  {, name_of_event } ; 
     */
    void event_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEVENT_DECLARATION);
        openNodeScope(node);
        consumeToken(EVENT);
        endToken = findTokenInBlock(SEMICOLON, endToken);;
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            name_of_event(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * event_expression <br>
     *     ::=  expression  <br>
     *     ||= <b>posedge</b>  scalar_event_expression  <br>
     *     ||= <b>negedge</b>  scalar_event_expression  <br>
     *     ||=  event_expression  <b>or</b>  event_expression  
     */
    void event_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEVENT_EXPRESSION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        Token tmpToken = findTokenInBlock(OR, endToken);
        if(tmpToken != null) {
            event_expression(node, tmpToken);
            new ASTtoken(node, tokenImage[OR]);
            consumeToken(OR);
            event_expression(node, endToken);
        }else if(kind == POSEDGE || kind == NEGEDGE) {
            new ASTtoken(node, tokenImage[kind]);
            consumeToken(kind);
            scalar_event_expression(node, endToken);
        }else {
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * expandrange <br>
     *     ::=  range  <br>
     *     ||= <b>scalared</b>  range  <br>
     *     ||= <b>vectored</b>  range  
     */
    void expandrange(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEXPANDRANGE);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == SCALARED || kind == VECTORED) {
            new ASTtoken(node, tokenMgr.getNextToken().image);
            consumeToken(kind);
        }
        range(node, endToken);
        closeNodeScope(node);
    }
    
    /**
     * expression <br>
     *     ::=  primary  <br>
     *     ||=  unary_operator   primary  <br>
     *     ||=  expression   binary_operator   expression  <br>
     *     ||=  expression  ?  expression  :  expression  <br>
     *     ||=  string  
     */
    void expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEXPRESSION);
        openNodeScope(node);
        Token tmpToken = null;
        if((tmpToken = findTokenInBlock(QUESTION, endToken)) != null) {
            expression(node, tmpToken);
            new ASTtoken(node, tokenImage[QUESTION]);
            consumeToken(QUESTION);
            tmpToken = findTokenInBlock(COLON, endToken);
            expression(node, tmpToken);
            new ASTtoken(node, tokenImage[COLON]);
            consumeToken(COLON);
            expression(node, endToken);
        }else if((tmpToken = find_binary_operator(endToken)) != null) {
            expression(node, tmpToken);
            binary_operator(node, endToken);
            expression(node, endToken);
        }else if(is_unary_operator()) {
            unary_operator(node, endToken);
            primary(node, endToken);
        }else if(tokenMgr.getNextTokenKind() == string_lexical) {
            new ASTtoken(node, tokenMgr.getNextToken().image);
            tokenMgr.toNextToken(); 
        }else {
            primary(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * <b>function</b> <br>
     *     ::= <b>function</b> [ range_or_type ]  name_of_function  ; <br>
     *         { tf_declaration }+ <br>
     *          statement  <br>
     *         <b>endfunction</b> 
     */
    void function(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFUNCTION);
        openNodeScope(node);
        consumeToken(FUNCTION);        
        endToken = findTokenInBlock(ENDFUNCTION, endToken);
        
        if(is_range_or_type()) {
            range_or_type(node, endToken);
        }
        
        Token tmpToken = findTokenInBlock(SEMICOLON, endToken);
        name_of_function(node, tmpToken);
        
        startBlock();
        consumeToken(SEMICOLON);
        while(is_tf_declaration()) {
            tmpToken = findTokenInBlock(SEMICOLON, endToken);
            tf_declaration(node, tmpToken);
        }
        
        statement(node, endToken);        
        endBlock();
        
        consumeToken(ENDFUNCTION);
        closeNodeScope(node);
        SymbolParser.parseSubProgramKind(node, this);
    }

    /**
     * function_call <br>
     *     ::=  name_of_function  (  expression  {, expression } ) <br>
     *     ||=  name_of_system_function  (  expression  {, expression } ) <br>
     *     ||=  name_of_system_function  
     */
    void function_call(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFUNCTION_CALL);
        Token tmpToken = null;
        openNodeScope(node);
        tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
        if(tmpToken == null) {
            name_of_system_function(node, endToken);
        }else {
            if(tokenMgr.getNextToken() == null) {
                throw new ParserException(tokenMgr.getCurrentToken());
            }
            if(tokenMgr.getNextToken().image.startsWith(tokenImage[DOLLAR])) {
                name_of_system_function(node, tmpToken);
            }else {
                name_of_function(node, tmpToken);
            }
            consumeToken(LPARENTHESIS);
            endToken = findTokenInBlock(RPARENTHESIS, endToken);
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
            consumeToken(RPARENTHESIS);
        }

        closeNodeScope(node);
    }

    /**
     * gate_declaration <br>
     *     ::=  gatetype  [ drive_strength ] [ delay ]  gate_instance  {, gate_instance } ; 
     */
    void gate_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGATE_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        
        gatetype(node, endToken);
        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            drive_strength(node, endToken);
        }
        if(is_delay()) {
            delay(node, endToken);
        }
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            gate_instance(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * gate_instance <br>
     *     ::= [ name_of_gate_instance ] (  terminal  {, terminal } ) 
     */
    void gate_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGATE_INSTANCE);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
        if(tokenMgr.getNextTokenKind(2) != LPARENTHESIS) {
            name_of_gate_instance(node, tmpToken);
        }
        consumeToken(LPARENTHESIS);
        endToken = findTokenInBlock(RPARENTHESIS, endToken);
        while(true) {
            tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            terminal(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * gatetype  is one of the following keywords: <br>
     *     <b>and</b> <b>nand</b> <b>or</b> <b>nor</b> <b>xor</b> <b>xnor</b> <br>
     *     <b>buf</b> <b>bufif0</b> <b>bufif1</b> <b>not</b> <b>notif0</b> <br>
     *     <b>notif1</b> <b>pulldown</b> <b>pullup</b> <br>
     *     <b>nmos</b> <b>rnmos</b> <b>pmos</b> <b>rpmos</b> <b>cmos</b>   <br>
     *     <b>rcmos</b> <b>tran</b> <b>rtran</b> <b>tranif0</b> <b>rtranif0</b> <br>
     *      <b>tranif1</b> <b>rtranif1</b> 
     */
    void gatetype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGATETYPE);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * identifier <br>
     *     ::=  IDENTIFIER {. IDENTIFIER } <br>
     *     (Note: the period may <b>not</b> be preceded <b>or</b> followed by a space.) 
     */
    void identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTidentifier);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(POINT, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            new ASTtoken(node, tokenMgr.getNextToken().image);
            consumeToken(IDENTIFIER);
            if(tokenMgr.getNextTokenKind() != POINT) {
                break;
            }
            consumeToken(POINT);
        }
        closeNodeScope(node);
    }

    /**
     * IDENTIFIER <br>
     *     An identifier is any sequence of letters, digits, dollar signs ($), <b>and</b> <br>
     *     underscore (_) symbol, except that the first must be a letter <b>or</b> the <br>
     *     underscore; the first character may <b>not</b> be a digit <b>or</b> $. Upper <b>and</b> lower <b>case</b> <br>
     *     letters are considered to be different. Identifiers may be up to 1024 <br>
     *     characters long. Some Verilog-based tools do <b>not</b> recognize identifier <br>
     *     characters beyond the 1024th as a significant part of the identifier. Escaped <br>
     *     identifiers start with the backslash character (\) <b>and</b> may include any <br>
     *     printable ASCII character. An escaped identifier ends with white space. The <br>
     *     leading backslash character is <b>not</b> considered to be part of the identifier. 
     */
//    void IDENTIFIER(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTIDENTIFIER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * init_val <br>
     *     ::= 1'b0 <br>
     *     ||= 1'b1 <br>
     *     ||= 1'bx <br>
     *     ||= 1'bX <br>
     *     ||= 1'B0 <br>
     *     ||= 1'B1 <br>
     *     ||= 1'Bx <br>
     *     ||= 1'BX <br>
     *     ||= 1 <br>
     *     ||= 0 
     */
    void init_val(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINIT_VAL);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * initial_statement <br>
     *     ::= <b>initial</b>  statement  
     */
    void initial_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINITIAL_STATEMENT);
        openNodeScope(node);
        consumeToken(INITIAL);
        statement(node, endToken);
        closeNodeScope(node);
    }

    /**
     * inout_declaration <br>
     *     ::= <b>inout</b> [ range ]  list_of_variables  ; 
     */
    void inout_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINOUT_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        consumeToken(INOUT);
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            range(node, endToken);
        }
        list_of_variables(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * input_declaration <br>
     *     ::= <b>input</b> [ range ]  list_of_variables  ; 
     */
    void input_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINPUT_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        consumeToken(INPUT);
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            range(node, endToken);
        }
        list_of_variables(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * input_identifier <br>
     *     ::= the  IDENTIFIER  of a <b>module</b> <b>input</b> <b>or</b> <b>inout</b> terminal 
     */
    void input_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINPUT_IDENTIFIER);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * input_list <br>
     *     ::=  level_input_list  <br>
     *     ||=  edge_input_list  
     */
    void input_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINPUT_LIST);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
        Token tmpToken1 = find_edge_symbol(endToken);
        if(tmpToken != null || tmpToken1 != null) {
            edge_input_list(node, endToken);
        }else {
            level_input_list(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * integer_declaration <br>
     *     ::= <b>integer</b>  list_of_register_variables  ; 
     */
    void integer_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTEGER_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        consumeToken(INTEGER);
        list_of_register_variables(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * level_input_list <br>
     *     ::= { level_symbol }+ 
     */
    void level_input_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLEVEL_INPUT_LIST);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * level_sensitive_path_declaration <br>
     *     ::= <b>if</b> ( conditional_port_expression ) <br>
     *         ( specify_input_terminal_descriptor  [ polarity_operator ] => <br>
     *          specify_output_terminal_descriptor ) =  path_delay_value ; <br>
     *     ||= <b>if</b> ( conditional_port_expression ) <br>
     *         ( list_of_path_inputs  [ polarity_operator ] *> <br>
     *          list_of_path_outputs ) =  path_delay_value ; <br>
     *     (Note: The following two symbols are literal symbols, <b>not</b> syntax description conventions:) <br>
     *         *> => 
     */
    void level_sensitive_path_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLEVEL_SENSITIVE_PATH_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        
        Token tmpToken = null;
        consumeToken(IF);
        consumeToken(LPARENTHESIS);
        tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
        conditional_port_expression(node, tmpToken);
        consumeToken(RPARENTHESIS);
        
        consumeToken(LPARENTHESIS);
        
        if((tmpToken = findTokenInBlock(EQ_ARROW, endToken)) != null) {
            specify_input_terminal_descriptor(node, tmpToken);
            if(is_polarity_operator()) {
                polarity_operator(node, endToken);
            }            
            consumeToken(EQ_ARROW);            
            tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            specify_output_terminal_descriptor(node, tmpToken);
        }else {
            tmpToken = findTokenInBlock(STAR_ARROW, endToken);
            list_of_path_inputs(node, tmpToken);
            if(is_polarity_operator()) {
                polarity_operator(node, endToken);
            }            
            consumeToken(STAR_ARROW);            
            tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            list_of_path_outputs(node, tmpToken);
        }
        consumeToken(RPARENTHESIS);        
        consumeToken(EQ);
        path_delay_value(node, endToken);
        
        closeNodeScope(node);
    }

    /**
     * level_symbol  is one of the following characters: <br>
     *     0 1 x X ? b B 
     */
//    void level_symbol(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTLEVEL_SYMBOL);
//        openNodeScope(node);
//        tokenMgr.toNextToken();
//        closeNodeScope(node);
//    }

    /**
     * list_of_assignments <br>
     *     ::=  assignment  {, assignment } 
     */
    void list_of_assignments(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_ASSIGNMENTS);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            assignment(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * list_of_module_connections <br>
     *     ::=  module_port_connection  {, module_port_connection } <br>
     *     ||=  named_port_connection  {, named_port_connection } 
     */
    void list_of_module_connections(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_MODULE_CONNECTIONS);
        openNodeScope(node);
        boolean is_named_port = (findToken(POINT, endToken) != null);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            if(is_named_port)
                named_port_connection(node, tmpToken);
            else
                module_port_connection(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * list_of_param_assignments <br>
     *     ::= param_assignment {, param_assignment } 
     */
    void list_of_param_assignments(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PARAM_ASSIGNMENTS);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            param_assignment(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * list_of_path_inputs <br>
     *     ::=  specify_input_terminal_descriptor  {, specify_input_terminal_descriptor } 
     */
    void list_of_path_inputs(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PATH_INPUTS);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            specify_input_terminal_descriptor(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * list_of_path_outputs <br>
     *     ::=  specify_output_terminal_descriptor  {, specify_output_terminal_descriptor } 
     */
    void list_of_path_outputs(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PATH_OUTPUTS);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            specify_output_terminal_descriptor(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * list_of_ports <br>
     *     ::= (  port  {, port } ) 
     */
    void list_of_ports(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PORTS);
        openNodeScope(node);
        consumeToken(LPARENTHESIS);
        endToken = findTokenInBlock(RPARENTHESIS, endToken);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            port(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * list_of_register_variables <br>
     *     ::=  register_variable  {, register_variable } 
     */
    void list_of_register_variables(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_REGISTER_VARIABLES);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            register_variable(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * list_of_variables <br>
     *    ::= name_of_variable {, name_of_variable}
     */
    void list_of_variables(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_VARIABLES);
        openNodeScope(node);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            name_of_variable(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * lvalue <br>
     *     ::=  IDENTIFIER  <br>
     *     ||=  IDENTIFIER  <b>[</b>  expression  <b>]</b> <br>
     *     ||=  IDENTIFIER  <b>[</b>  constant_expression  :  constant_expression  <b>]</b> <br>
     *     ||=  concatenation  
     */
    void lvalue(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLVALUE);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == LBRACE) {
            concatenation(node, endToken);
        }else {
            new ASTtoken(node, tokenMgr.getNextToken().image);
            tokenMgr.toNextToken();
            if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
                consumeToken(LSQUARE_BRACKET);
                endToken = findTokenInBlock(RSQUARE_BRACKET, endToken);
                Token tmpToken = findTokenInBlock(COLON, endToken);
                if(tmpToken != null) {
                    constant_expression(node, tmpToken);
                    consumeToken(COLON);
                    constant_expression(node, endToken);
                }else {
                    expression(node, endToken);
                }
                consumeToken(RSQUARE_BRACKET);
            }
        }
        closeNodeScope(node);
    }

    /**
     * mintypmax_expression <br>
     *     ::=  expression  <br>
     *     ||=  expression  :  expression  :  expression  
     */
    void mintypmax_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMINTYPMAX_EXPRESSION);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(COLON, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        else
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(tmpToken), COLON, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        expression(node, tmpToken);
        
        if(tokenMgr.getNextTokenKind() == COLON) {
            consumeToken(COLON);
            tmpToken = findTokenInBlock(COLON, endToken);
            expression(node, tmpToken);
            consumeToken(COLON);
            expression(node, endToken);
        }
     
        closeNodeScope(node);
    }

    /**
     * <b>module</b> <br>
     *     ::= <b>module</b>  name_of_module  [ list_of_ports ] ; <br>
     *         { module_item } <br>
     *         <b>endmodule</b> <br>
     *     ||= <b>macromodule</b>  name_of_module  [ list_of_ports ] ; <br>
     *         { module_item } <br>
     *         <b>endmodule</b> 
     */
    void module(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind != MODULE && kind != MACROMODULE) {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        
        new ASTtoken(node, tokenImage[kind]);
        consumeToken(kind);
        
        endToken = findTokenInBlock(ENDMODULE, endToken);
        Token tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
        if(tmpToken == null)
            tmpToken = findTokenInBlock(SEMICOLON, endToken);
        name_of_module(node, tmpToken);
        String name = node.getLastChild().firstTokenImage();
        
        if(!parseSymbol) {
            startBlock();
            tmpToken = findTokenInBlock(SEMICOLON, endToken);
            if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
                list_of_ports(node, tmpToken);
            }
            
            consumeToken(SEMICOLON);
            while(tokenMgr.getNextTokenKind() != ENDMODULE) {
                module_item(node, endToken);
            }
            endBlock();
            consumeToken(ENDMODULE);
        }else {
            tokenMgr.setCurrentToken(endToken);
        }
        
        closeNodeScope(node);
        
        curSymbolTable.addSymbol(new Symbol(name, kind));
    }

    /**
     * module_instance <br>
     *     ::=  name_of_instance  ( [ list_of_module_connections ] ) 
     */
    void module_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_INSTANCE);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
        name_of_instance(node, tmpToken);
        consumeToken(LPARENTHESIS);
        endToken = findTokenInBlock(RPARENTHESIS, endToken);
        list_of_module_connections(node, endToken);
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * module_instantiation <br>
     *     ::=  name_of_module  [ parameter_value_assignment ] <br>
     *          module_instance  {, module_instance } ; 
     */
    void module_instantiation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_INSTANTIATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        
        name_of_module(node, endToken);
        if(tokenMgr.getNextTokenKind() == PARA) {
            parameter_value_assignment(node, endToken);
        }
        
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            module_instance(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }

        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * module_item <br>
     *     ::=  parameter_declaration  <br>
     *     ||=  input_declaration  <br>
     *     ||=  output_declaration  <br>
     *     ||=  inout_declaration  <br>
     *     ||=  net_declaration  <br>
     *     ||=  reg_declaration  <br>
     *     ||=  time_declaration  <br>
     *     ||=  integer_declaration  <br>
     *     ||=  real_declaration  <br>
     *     ||=  event_declaration  <br>
     *     ||=  gate_declaration  <br>
     *     ||=  udp_instantiation  <br>
     *     ||=  module_instantiation  <br>
     *     ||=  parameter_override  <br>
     *     ||=  continuous_assign  <br>
     *     ||=  specify_block  <br>
     *     ||=  initial_statement  <br>
     *     ||=  always_statement  <br>
     *     ||=  task  <br>
     *     ||=  function  
     */
    void module_item(IASTNode p, Token endToken) throws ParserException {
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
        ASTNode node = new ASTNode(p, ASTMODULE_ITEM);
        openNodeScope(node);
        
       
        int kind = tokenMgr.getNextTokenKind();
        switch(kind)
        {
        case PARAMETER:
            parameter_declaration(node, endToken);
            break;
        case INPUT:
            input_declaration(node, endToken);
            break;
        case OUTPUT:
            output_declaration(node, endToken);
            break;
        case INOUT:
            inout_declaration(node, endToken);
            break;
        case TRIREG:
            net_declaration(node, endToken);
            break;
        case REG:
            reg_declaration(node, endToken);
            break;
        case TIME:
            time_declaration (node, endToken);
            break;
        case INTEGER:
            integer_declaration(node, endToken);
            break;
        case REAL:
            real_declaration(node, endToken);
            break;
        case EVENT:
            event_declaration(node, endToken);
            break;
        case DEFPARAM:
            parameter_override(node, endToken);
            break;
        case ASSIGN:
            continuous_assign(node, endToken);
            break;
        case SPECIFY:
            specify_block(node, endToken);
            break;
        case INITIAL:
            initial_statement(node, endToken);
            break;
        case ALWAYS:
            always_statement(node, endToken);
            break;
        case TASK:
            task(node, endToken);
            break;
        case FUNCTION:
            function(node, endToken);
            break;
        default:
            endToken = findTokenInBlock(SEMICOLON, endToken);
            endToken = tokenMgr.getNextToken(endToken);
            if(find_nettype(endToken) != null) {
                net_declaration(node, endToken);
            }else if(find_gatetype(endToken) != null) {
                gate_declaration(node, endToken);
            }else {
                //TODO: check udp or module
                module_instantiation(node, endToken);
                //udp_instantiation(node, endToken);
            }
            break;
        }

        closeNodeScope(node);
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
    }

    /**
     * module_port_connection <br>
     *     ::=  expression  <br>
     *     ||=  NULL  
     */
    void module_port_connection(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_PORT_CONNECTION);
        openNodeScope(node);
        if(tokenMgr.getNextToken() != endToken) {
            expression(node, endToken);
        }else {
            NULL(node, endToken);   // null always before comma
        }
        closeNodeScope(node);
    }

    /**
     * multiple_concatenation <br>
     *     ::= <b>{</b>  expression  <b>{</b>  expression  {, expression } <b>}</b> <b>}</b> 
     */
    void multiple_concatenation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMULTIPLE_CONCATENATION);
        openNodeScope(node);
        consumeToken(LBRACE);
        endToken = findTokenInBlock(RBRACE, endToken);
        
        Token tmpToken = findTokenInBlock(LBRACE, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        expression(node, tmpToken);

        consumeToken(LBRACE);
        Token tmpToken1 = findTokenInBlock(RBRACE, endToken);
        while(true) {
            tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = tmpToken1;
            expression(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RBRACE);

        consumeToken(RBRACE);
        closeNodeScope(node);
    }

    /**
     * name_of_block <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_block(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_BLOCK);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_event <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_event(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_EVENT);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_function <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_function(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_FUNCTION);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_gate_instance <br>
     *     ::=  IDENTIFIER [ range ] 
     */
    void name_of_gate_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_GATE_INSTANCE);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            range(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * name_of_instance <br>
     *     ::=  IDENTIFIER [ range ] 
     */
    void name_of_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_INSTANCE);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            range(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * name_of_memory <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_memory(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_MEMORY);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_module <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_module(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_MODULE);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_port <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_port(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_PORT);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_register <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_register(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_REGISTER);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_system_function <br>
     *     ::= $ system_identifier  <br>
     *     (Note: the $ may <b>not</b> be followed by a space.) 
     */
    void name_of_system_function(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_SYSTEM_FUNCTION);
        openNodeScope(node);
        Token token = tokenMgr.getNextToken();
        if(token == null || !token.image.startsWith(tokenImage[DOLLAR])) {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        new ASTtoken(node, token.image.substring(1));   // ignore $
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_system_task <br>
     *     ::= $ system_identifier  (Note: the $ may <b>not</b> be followed by a space.) 
     */
    void name_of_system_task(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_SYSTEM_TASK);
        openNodeScope(node);
        Token token = tokenMgr.getNextToken();
        if(token == null || !token.image.startsWith(tokenImage[DOLLAR])) {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        new ASTtoken(node, token.image.substring(1));   // ignore $
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_task <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_task(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_TASK);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_udp <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_udp(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_UDP);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * name_of_udp_instance <br>
     *     ::=  IDENTIFIER [ range ] 
     */
    void name_of_udp_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_UDP_INSTANCE);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            range(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * name_of_variable <br>
     *     ::=  IDENTIFIER  
     */
    void name_of_variable(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_VARIABLE);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * named_port_connection <br>
     *     ::= . IDENTIFIER  (  [ expression ]  ) 
     */
    void named_port_connection(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAMED_PORT_CONNECTION);
        openNodeScope(node);
        consumeToken(POINT);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        
        consumeToken(LPARENTHESIS);
        endToken = findTokenInBlock(RPARENTHESIS, endToken);
        if(tokenMgr.getNextToken() != endToken) {
            expression(node, endToken);
        }
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * net_declaration <br>
     *     ::=  nettype  [ expandrange ] [ delay ]  list_of_variables  ; <br>
     *     ||= <b>trireg</b> [ charge_strength ] [ expandrange ] [ delay ] list_of_variables  ; <br>
     *     ||=  nettype  [ drive_strength ] [ expandrange ] [ delay ]  list_of_assignments  ; 
     */
    void net_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNET_DECLARATION);
        openNodeScope(node);

        endToken = findTokenInBlock(SEMICOLON, endToken);
        if(tokenMgr.getNextTokenKind() == TRIREG) {
            consumeToken(TRIREG);
        }else {
            nettype(node, endToken);
        }

        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            int kind = tokenMgr.getNextTokenKind(2);
            if(!(kind == SMALL || kind == MEDIUM || kind == LARGE))
                drive_strength(node, endToken);
        }

        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            charge_strength(node, endToken);
        }

        if(is_expandrange()) {
            expandrange(node, endToken);
        }
        
        if(is_delay()) {
            delay(node, endToken);
        }
        
        if(findTokenInBlock(EQ, endToken) != null) {
            list_of_assignments(node, endToken);
        }else {
            list_of_variables(node, endToken);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * nettype  is one of the following keywords: <br>
     *     <b>wire</b> <b>tri</b> <b>tri1</b> <b>supply0</b> <b>wand</b> <b>triand</b> <br>
     *     <b>tri0</b> <b>supply1</b> <b>wor</b> <b>trior</b> <b>trireg</b> 
     */
    void nettype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNETTYPE);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * next_state <br>
     *     ::=  output_symbol  <br>
     *     ||= - (This is a literal hyphen, see Chapter 5 <b>for</b> details). 
     */
    void next_state(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNEXT_STATE);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() != SUB) {
            output_symbol(node, endToken);
        }else {
            tokenMgr.toNextToken(); // hyphen
        }
        closeNodeScope(node);
    }

    /**
     * non_blocking_assignment <br>
     *     ::=  lvalue  <=  expression  ;<br>
     *     ||=  lvalue  <=  delay_or_event_control   expression 
     */
    void non_blocking_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNON_BLOCKING_ASSIGNMENT);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(LE, endToken);
        lvalue(node, tmpToken);
        consumeToken(LE);
        
        tmpToken = find_delay_or_event(endToken);
        if(tmpToken != null) {
            delay_or_event_control(node, endToken); //TODO: endToken ?
            expression(node, endToken);
        }else {
            expression(node, endToken);
        }

        closeNodeScope(node);
    }

    /**
     * notify_register <br>
     *     ::=  IDENTIFIER  
     */
    void notify_register(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNOTIFY_REGISTER);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * NULL <br>
     *     ::= nothing - this form covers the <b>case</b> of an empty item in a list - <b>for</b> example: <br>
     *           (a, b, , d) 
     */
    void NULL(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNULL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * number <br>
     *     ::=  decimal_number  <br>
     *     ||= [ unsigned_number ]  base   unsigned_number  <br>
     *     ||=  decimal_number . unsigned_number  <br>
     *     ||=  decimal_number [. unsigned_number ] <br>
     *         E decimal_number  <br>
     *     ||=  decimal_number [. unsigned_number ] <br>
     *         e decimal_number  <br>
     *     (Note: embedded spaces are illegal in Verilog numbers, but embedded underscore <br>
     *     characters can be used <b>for</b> spacing in any type of number.) 
     */
//    void number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTNUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * output_declaration <br>
     *     ::= <b>output</b> [ range ]  list_of_variables  ; 
     */
    void output_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOUTPUT_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        consumeToken(OUTPUT);
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            range(node, endToken);
        }
        list_of_variables(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * output_identifier <br>
     *     ::= the  IDENTIFIER  of a <b>module</b> <b>output</b> <b>or</b> <b>inout</b> terminal. 
     */
    void output_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOUTPUT_IDENTIFIER);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * output_symbol  is one of the following characters: <br>
     *     0 1 x X 
     */
    void output_symbol(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOUTPUT_SYMBOL);
        openNodeScope(node);
        if(find_output_symbol(endToken) == null) {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * output_terminal_name <br>
     *     ::=  name_of_variable  
     */
    void output_terminal_name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOUTPUT_TERMINAL_NAME);
        openNodeScope(node);
        name_of_variable(node, endToken);
        closeNodeScope(node);
    }

    /**
     * par_block <br>
     *     ::= <b>fork</b> { statement } <b>join</b> <br>
     *     ||= <b>fork</b> :  name_of_block  { block_declaration } { statement } <b>join</b> 
     */
    void par_block(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPAR_BLOCK);
        openNodeScope(node);
        consumeToken(FORK);
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
        endToken = findTokenInBlock(JOIN, endToken);
        if(tokenMgr.getNextTokenKind() != COLON) {
            while(tokenMgr.getNextTokenKind() != JOIN) {
                statement(node, endToken);
            }
        }else {
            consumeToken(JOIN);
            name_of_block(node, endToken);
            while(is_block_declaration()) {
                block_declaration(node, endToken);
            }
            while(tokenMgr.getNextTokenKind() != JOIN) {
                statement(node, endToken);
            }
        }
        
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
        consumeToken(JOIN);
        closeNodeScope(node);
    }

    /**
     * param_assignment <br>
     *     ::= IDENTIFIER  =  constant_expression  
     */
    void param_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAM_ASSIGNMENT);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        consumeToken(EQ);
        constant_expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * parameter_declaration <br>
     *     ::= <b>parameter</b> [ range ] list_of_param_assignments  ; 
     */
    void parameter_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAMETER_DECLARATION);
        
        openNodeScope(node);
        consumeToken(PARAMETER);
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            Token tmpToken = findTokenInBlock(RSQUARE_BRACKET, endToken);
            range(node, tmpToken);
        }
        endToken = findTokenInBlock(SEMICOLON, endToken);
        list_of_param_assignments(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * parameter_override <br>
     *     ::= <b>defparam</b>  list_of_param_assignments  ; 
     */
    void parameter_override(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAMETER_OVERRIDE);
        openNodeScope(node);
        consumeToken(DEFPARAM);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        list_of_param_assignments(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * parameter_value_assignment <br>
     *     ::= # (  expression  {, expression } ) 
     */
    void parameter_value_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAMETER_VALUE_ASSIGNMENT);
        openNodeScope(node);
        consumeToken(PARA);
        
        consumeToken(LPARENTHESIS);
        endToken = findTokenInBlock(RPARENTHESIS, endToken);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            expression(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
            
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * path_declaration <br>
     *     ::=  path_description  =  path_delay_value  ; 
     */
    void path_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPATH_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        Token tmpToken = findTokenInBlock(EQ, endToken);
        path_description(node, tmpToken);
        consumeToken(EQ);
        path_delay_value(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * path_delay_expression <br>
     *     ::=  mintypmax_expression  
     */
    void path_delay_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPATH_DELAY_EXPRESSION);
        openNodeScope(node);
        mintypmax_expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * path_delay_value <br>
     *     ::=  path_delay_expression  <br>
     *     ||= (  path_delay_expression ,  path_delay_expression  ) <br>
     *     ||= (  path_delay_expression ,  path_delay_expression , <br>
     *          path_delay_expression ) <br>
     *     ||= (  path_delay_expression ,  path_delay_expression , <br>
     *          path_delay_expression ,  path_delay_expression , <br>
     *          path_delay_expression ,  path_delay_expression  ) <br>
     *     ||= (  path_delay_expression ,  path_delay_expression , <br>
     *          path_delay_expression ,  path_delay_expression , <br>
     *          path_delay_expression ,  path_delay_expression , <br>
     *          path_delay_expression ,  path_delay_expression , <br>
     *          path_delay_expression ,  path_delay_expression , <br>
     *          path_delay_expression ,  path_delay_expression  ) 
     */
    void path_delay_value(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPATH_DELAY_VALUE);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() != LPARENTHESIS) {
            path_delay_expression(node, endToken);
        }else {
            consumeToken(LPARENTHESIS);
            endToken = findTokenInBlock(RPARENTHESIS, endToken);
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                path_delay_expression(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
            consumeToken(RPARENTHESIS);
        }
        closeNodeScope(node);
    }

    /**
     * path_description <br>
     *     ::= (  specify_input_terminal_descriptor  =>  specify_output_terminal_descriptor  ) <br>
     *     ||= (  list_of_path_inputs  *>  list_of_path_outputs  ) 
     */
    void path_description(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPATH_DESCRIPTION);
        openNodeScope(node);
        consumeToken(LPARENTHESIS);
        endToken = findTokenInBlock(RPARENTHESIS, endToken);
        
        Token tmpToken = null;
        if((tmpToken = findTokenInBlock(EQ_ARROW, endToken)) != null) {
            specify_input_terminal_descriptor(node, tmpToken);
            consumeToken(EQ_ARROW);
            specify_output_terminal_descriptor(node, endToken);
        }else {
            tmpToken = findTokenInBlock(STAR_ARROW, endToken);
            list_of_path_inputs(node, tmpToken);
            consumeToken(STAR_ARROW);
            list_of_path_outputs(node, endToken);
        }
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * polarity_operator <br>
     *     ::= + <br>
     *     ||= - 
     */
    void polarity_operator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPOLARITY_OPERATOR);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * port <br>
     *     ::= [ port_expression ] <br>
     *     ||= .  name_of_port  ( [ port_expression ] ) 
     */
    void port(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() != POINT) {
            if(tokenMgr.getNextToken() != endToken) {
               port_expression(node, endToken);
            }
        }else {
            consumeToken(POINT);
            Token tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
            name_of_port(node, tmpToken);
            consumeToken(LPARENTHESIS);
            if(tokenMgr.getNextTokenKind() != RPARENTHESIS) {
                endToken = findTokenInBlock(RPARENTHESIS, endToken);
                port_expression(node, endToken);
            }
            consumeToken(RPARENTHESIS);
        }
        closeNodeScope(node);
    }

    /**
     * port_expression <br>
     *     ::=  port_reference  <br>
     *     ||= <b>{</b>  port_reference  {, port_reference } <b>}</b> 
     */
    void port_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT_EXPRESSION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() != LBRACE) {
            port_reference(node, endToken);
        }else {
            new ASTtoken(node, tokenImage[LBRACE]);
            consumeToken(LBRACE);
            endToken = findTokenInBlock(RBRACE, endToken);
            while(true) {
                Token tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                port_reference(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
            consumeToken(RBRACE);
        }
        closeNodeScope(node);
    }

    /**
     * port_reference <br>
     *     ::=  name_of_variable  <br>
     *     ||=  name_of_variable  <b>[</b>  constant_expression  <b>]</b> <br>
     *     ||=  name_of_variable  <b>[</b>  constant_expression  : constant_expression  <b>]</b> 
     */
    void port_reference(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT_REFERENCE);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(LSQUARE_BRACKET, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        name_of_variable(node, tmpToken);
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            consumeToken(LSQUARE_BRACKET);
            endToken = findTokenInBlock(RSQUARE_BRACKET, endToken);
            tmpToken = findTokenInBlock(COLON, endToken);
            if(tmpToken == null) {
                constant_expression(node, endToken);
            }else {
                constant_expression(node, tmpToken);
                consumeToken(COLON);
                constant_expression(node, endToken);
            }
            consumeToken(RSQUARE_BRACKET);
        }
        closeNodeScope(node);
    }

    /**
     * primary <br>
     *     ::=  number  <br>
     *     ||=  IDENTIFIER  <br>
     *     ||=  IDENTIFIER  <b>[</b>  expression  <b>]</b> <br>
     *     ||=  IDENTIFIER  <b>[</b>  constant_expression  :  constant_expression  <b>]</b> <br>
     *     ||=  concatenation  <br>
     *     ||=  multiple_concatenation  <br>
     *     ||=  function_call  <br>
     *     ||= (  mintypmax_expression  ) 
     */
    void primary(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPRIMARY);
        openNodeScope(node);
        Token tmpToken = null;
        int kind = tokenMgr.getNextTokenKind();
        if(kind == LBRACE) {
            tmpToken = findTokenInBlock(tokenMgr.getNextToken(2), LBRACE, endToken);
            if(tmpToken == null) {
                concatenation(node, endToken);
            }else {
                multiple_concatenation(node, endToken);
            }
        }else if(kind == LPARENTHESIS) {
            consumeToken(LPARENTHESIS);
            tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            mintypmax_expression(node, tmpToken);
            consumeToken(RPARENTHESIS);
        }else if((tmpToken = findTokenInBlock(LSQUARE_BRACKET, endToken)) != null) {
            new ASTtoken(node, tokenMgr.getNextToken().image);
            tokenMgr.toNextToken();
            consumeToken(LSQUARE_BRACKET);
            endToken = findTokenInBlock(RSQUARE_BRACKET, endToken);
            tmpToken = findTokenInBlock(COLON, endToken);
            if(tmpToken == null) {
                expression(node, endToken);
            }else {
                constant_expression(node, tmpToken);
                consumeToken(COLON);
                constant_expression(node, endToken);
            }
            consumeToken(RSQUARE_BRACKET);
        }else if((tmpToken = findTokenInBlock(LPARENTHESIS, endToken)) != null) {
            function_call(node, endToken);
        }else {
            if(tokenMgr.getNextToken().image.startsWith(tokenImage[DOLLAR])) {
                function_call(node, endToken);
            }else {
                new ASTtoken(node, tokenMgr.getNextToken().image);
                tokenMgr.toNextToken();
            }
        }
        closeNodeScope(node);
    }

    /**
     * range <br>
     *     ::= <b>[</b>  constant_expression  :  constant_expression  <b>]</b> 
     */
    void range(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRANGE);
        openNodeScope(node);
        consumeToken(LSQUARE_BRACKET);
        endToken = findTokenInBlock(RSQUARE_BRACKET, endToken);
        Token tmpToken = findTokenInBlock(COLON, endToken);
        constant_expression(node, tmpToken);
        consumeToken(COLON);
        constant_expression(node, endToken);
        consumeToken(RSQUARE_BRACKET);
        closeNodeScope(node);
    }

    /**
     * range_or_type <br>
     *     ::=  range  <br>
     *     ||= <b>integer</b> <br>
     *     ||= <b>real</b> 
     */
    void range_or_type(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRANGE_OR_TYPE);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == LSQUARE_BRACKET) {
            range(node, endToken);
        }else if(kind == INTEGER || kind == REAL){
            new ASTtoken(node, tokenMgr.getNextToken().image);
            tokenMgr.toNextToken();
        }else {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        closeNodeScope(node);
    }
    
    /**
     * real_declaration <br>
     *     ::= <b>real</b>  list_of_variables  ; 
     */
    void real_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREAL_DECLARATION);
        openNodeScope(node);
        consumeToken(REAL);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        list_of_variables(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * reg_declaration <br>
     *     ::= <b>reg</b> [ range ]  list_of_register_variables  ; 
     */
    void reg_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREG_DECLARATION);
        
        openNodeScope(node);
        consumeToken(REG);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        if(tokenMgr.getNextTokenKind() == LSQUARE_BRACKET) {
            range(node, endToken);
        }
        
        list_of_register_variables(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * register_variable <br>
     *     ::=  name_of_register  <br>
     *     ||=  name_of_memory  range 
     */
    void register_variable(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREGISTER_VARIABLE);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(LSQUARE_BRACKET, endToken);
        if(tmpToken != null) {
            name_of_memory(node, tmpToken);
            range(node, endToken);
        }else {
            name_of_register(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * scalar_constant <br>
     *     ::= 1'b0 <br>
     *     ||= 1'b1 <br>
     *     ||= 1'B0 <br>
     *     ||= 1'B1 <br>
     *     ||= 'b0 <br>
     *     ||= 'b1 <br>
     *     ||= 'B0 <br>
     *     ||= 'B1 <br>
     *     ||= 1 <br>
     *     ||= 0 
     */
    void scalar_constant(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSCALAR_CONSTANT);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * scalar_event_expression <br>
     *     Scalar <b>event</b> expression is an expression that resolves to a one bit value.
     */
    void scalar_event_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSCALAR_EVENT_EXPRESSION);
        openNodeScope(node);
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * scalar_expression <br>
     *     A scalar expression is a one bit net <b>or</b> a bit-select of an expanded vector net. 
     */
    void scalar_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSCALAR_EXPRESSION);
        openNodeScope(node);
        expression(node, endToken); //TODO: ??
        closeNodeScope(node);
    }

    /**
     * scalar_timing_check_condition <br>
     *     ::=  scalar_expression  <br>
     *     ||= ~ scalar_expression  <br>
     *     ||=  scalar_expression  ==  scalar_constant  <br>
     *     ||=  scalar_expression  ===  scalar_constant  <br>
     *     ||=  scalar_expression  !=  scalar_constant  <br>
     *     ||=  scalar_expression  !==  scalar_constant  
     */
    void scalar_timing_check_condition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSCALAR_TIMING_CHECK_CONDITION);
        openNodeScope(node);
        Token tmpToken = null;
        if(tokenMgr.getNextTokenKind() == BIT_NEG) {
            new ASTtoken(node, tokenImage[BIT_NEG]);
            consumeToken(BIT_NEG);
            scalar_expression(node, endToken);
        }else if((tmpToken = findTokenInBlock(LOGIC_EQ, endToken)) != null) {
            scalar_expression(node, tmpToken);
            consumeToken(LOGIC_EQ);
            scalar_expression(node, endToken);
        }else if((tmpToken = findTokenInBlock(LOGIC_NEQ, endToken)) != null) {
            scalar_expression(node, tmpToken);
            consumeToken(LOGIC_NEQ);
            scalar_expression(node, endToken);
        }else if((tmpToken = findTokenInBlock(CASE_EQ, endToken)) != null) {
            scalar_expression(node, tmpToken);
            consumeToken(CASE_EQ);
            scalar_expression(node, endToken);
        }else if((tmpToken = findTokenInBlock(CASE_NEQ, endToken)) != null) {
            scalar_expression(node, tmpToken);
            consumeToken(CASE_NEQ);
            scalar_expression(node, endToken);
        }else {
            scalar_expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * sdpd <br>
     *     ::= <b>if</b> ( sdpd_conditional_expression ) path_description = path_delay_value ; 
     */
    void sdpd(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSDPD);
        openNodeScope(node);
        
        endToken = findTokenInBlock(SEMICOLON, endToken);
        consumeToken(IF);
        consumeToken(LPARENTHESIS);
        Token tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
        sdpd_conditional_expression(node, tmpToken);
        consumeToken(RPARENTHESIS);
        
        tmpToken = findTokenInBlock(EQ, endToken);
        path_description(node, tmpToken);
        consumeToken(EQ);
        path_delay_value(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * sdpd_conditional_expression <br>
     *     ::= expression  binary_operator  expression  <br>
     *     ||= unary_operator  expression  
     */
    void sdpd_conditional_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSDPD_CONDITIONAL_EXPRESSION);
        openNodeScope(node);
        if(is_unary_operator()) {
            unary_operator(node, endToken);
            expression(node, endToken);
        }else {
            Token tmpToken = find_binary_operator(endToken);
            expression(node, tmpToken);
            expression(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * seq_block <br>
     *     ::= <b>begin</b> { statement } <b>end</b> <br>
     *     ||= <b>begin</b> :  name_of_block  { block_declaration } { statement } <b>end</b> 
     */
    void seq_block(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSEQ_BLOCK);
        openNodeScope(node);
        consumeToken(BEGIN);
        startBlock();
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
        endToken = findTokenInBlock(END, endToken);
        if(endToken == null) {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        
        Token tmpToken = findTokenInBlock(COLON, endToken);
        if(tmpToken == null) {
            while(tokenMgr.getNextTokenKind() != END) {
                statement(node, endToken);
            }
        }else {
            consumeToken(COLON);
            name_of_block(node, endToken);
            while(is_block_declaration()) {
                block_declaration(node, endToken);
            }
            while(tokenMgr.getNextTokenKind() != END) {
                statement(node, endToken);
            }
        }
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
        endBlock();
        consumeToken(END);
        closeNodeScope(node);
    }

    /**
     * sequential_entry <br>
     *     ::=  input_list  :  state  :  next_state  ; 
     */
    void sequential_entry(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSEQUENTIAL_ENTRY);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        Token tmpToken = findTokenInBlock(COLON, endToken);
        input_list(node, tmpToken);
        consumeToken(COLON);
        tmpToken = findTokenInBlock(COLON, endToken);
        state(node, tmpToken);
        consumeToken(COLON);
        next_state(node, tmpToken);
        closeNodeScope(node);
    }

    /**
     * source_text <br>
     *     ::= { description } 
     */
    ASTNode source_text() throws ParserException {
        ASTNode node = new ASTNode(null, ASTSOURCE_TEXT);
        openNodeScope(node);
        while(tokenMgr.getNextToken() != null) {
            if(is_description()) {
                description(node, null);
            }else if(is_compiler_directives()) {
                compiler_directives(node, null);
            }
        }
        closeNodeScope(node);
        return node;
    }

    /**
     * specify_block <br>
     *     ::= <b>specify</b> { specify_item } <b>endspecify</b> 
     */
    void specify_block(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_BLOCK);
        openNodeScope(node);
        consumeToken(SPECIFY);
        startBlock();
        endToken = findTokenInBlock(ENDSPECIFY, endToken);
        while(tokenMgr.getNextTokenKind() != ENDSPECIFY) {
            specify_item(node, endToken);
        }
        endBlock();
        consumeToken(ENDSPECIFY);
        closeNodeScope(node);
    }

    /**
     * specify_input_terminal_descriptor <br>
     *     ::=  input_identifier  <br>
     *     ||=  input_identifier  <b>[</b>  constant_expression  <b>]</b> <br>
     *     ||=  input_identifier  <b>[</b>  constant_expression  :  constant_expression  <b>]</b> 
     */
    void specify_input_terminal_descriptor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_INPUT_TERMINAL_DESCRIPTOR);
        openNodeScope(node);
        Token tmpToken = null;
        if((tmpToken = findTokenInBlock(LSQUARE_BRACKET, endToken)) != null) {
            input_identifier(node, tmpToken);
            consumeToken(LSQUARE_BRACKET);
            endToken = findTokenInBlock(RSQUARE_BRACKET, endToken);
            tmpToken = findTokenInBlock(COLON, endToken);
            if(tmpToken == null) {
                constant_expression(node, endToken);
            }else {
                constant_expression(node, tmpToken);
                consumeToken(COLON);
                constant_expression(node, endToken);
            }
            consumeToken(RSQUARE_BRACKET);
        }else {
            input_identifier(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * specify_item <br>
     *     ::=  specparam_declaration  <br>
     *     ||=  path_declaration  <br>
     *     ||=  level_sensitive_path_declaration  <br>
     *     ||=  edge_sensitive_path_declaration  <br>
     *     ||=  system_timing_check  <br>
     *     ||=  sdpd  
     */
    void specify_item(IASTNode p, Token endToken) throws ParserException {
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
            return;
        }
        ASTNode node = new ASTNode(p, ASTSPECIFY_ITEM);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        if(tokenMgr.getNextTokenKind() == SPECPARAM) {
            specparam_declaration(node, endToken);
        }else if(tokenMgr.getNextToken().image.startsWith(tokenImage[DOLLAR])) {
            system_timing_check(node, endToken);
        }else {
            //TODO: different several path
            edge_sensitive_path_declaration(node, endToken);
            //path_declaration(node, endToken);
            //level_sensitive_path_declaration(node, endToken);
            //sdpd(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * specify_output_terminal_descriptor <br>
     *     ::=  output_identifier  <br>
     *     ||=  output_identifier  <b>[</b>  constant_expression  <b>]</b> <br>
     *     ||=  output_identifier  <b>[</b>  constant_expression  :  constant_expression  <b>]</b> 
     */
    void specify_output_terminal_descriptor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_OUTPUT_TERMINAL_DESCRIPTOR);
        openNodeScope(node);
        Token tmpToken = null;
        if((tmpToken = findTokenInBlock(LSQUARE_BRACKET, endToken)) != null) {
            output_identifier(node, tmpToken);
            consumeToken(LSQUARE_BRACKET);
            endToken = findTokenInBlock(RSQUARE_BRACKET, endToken);
            tmpToken = findTokenInBlock(COLON, endToken);
            if(tmpToken == null) {
                constant_expression(node, endToken);
            }else {
                constant_expression(node, tmpToken);
                consumeToken(COLON);
                constant_expression(node, endToken);
            }
            consumeToken(RSQUARE_BRACKET);
        }else {
            output_identifier(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * specify_terminal_descriptor <br>
     *     ::=  specify_input_terminal_descriptor  <br>
     *     ||= specify_output_terminal_descriptor  
     */
    void specify_terminal_descriptor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_TERMINAL_DESCRIPTOR);
        openNodeScope(node);
        //TODO:check input/output terminal
        specify_input_terminal_descriptor(node, endToken);
        //specify_output_terminal_descriptor(node, endToken);
        closeNodeScope(node);
    }

    /**
     * specparam_declaration <br>
     *     ::= <b>specparam</b>  list_of_param_assignments  ; 
     */
    void specparam_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECPARAM_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        consumeToken(SPECPARAM);
        list_of_param_assignments(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * state <br>
     *     ::=  level_symbol  
     */
    void state(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTATE);
        openNodeScope(node);
        if(!is_level_symbol_array()) {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * statement <br>
     *     ::= blocking_assignment  ; <br>
     *     ||=  non_blocking_assignment  ; <br>
     *     ||= <b>if</b> (  expression  )  statement_or_null  <br>
     *     ||= <b>if</b> (  expression  )  statement_or_null  <b>else</b>  statement_or_null  <br>
     *     ||= <b>case</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
     *     ||= <b>casez</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
     *     ||= <b>casex</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
     *     ||= <b>forever</b>  statement  <br>
     *     ||= <b>repeat</b> (  expression  )  statement  <br>
     *     ||= <b>while</b> (  expression  )  statement  <br>
     *     ||= <b>for</b> (  assignment  ;  expression  ;  assignment  )  statement  <br>
     *     ||=  delay_or_event_control   statement_or_null  <br>
     *     ||= <b>wait</b> (  expression  )  statement_or_null  <br>
     *     ||= ->  name_of_event  ; <br>
     *     ||=  seq_block  <br>
     *     ||=  par_block  <br>
     *     ||=  task_enable  <br>
     *     ||=  system_task_enable  <br>
     *     ||= <b>disable</b>  name_of_task  ; <br>
     *     ||= <b>disable</b>  name_of_block  ; <br>
     *     ||= <b>assign</b>  assignment  ; <br>
     *     ||= <b>deassign</b>  lvalue  ; <br>
     *     ||= <b>force</b>  assignment  ; <br>
     *     ||= <b>release</b>  lvalue  ; 
     */
    void statement(IASTNode p, Token endToken) throws ParserException {
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
        
        ASTNode node = new ASTNode(p, ASTSTATEMENT);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        
        switch(kind)
        {
        case IF:
            if_statement(node, endToken);
            break;
            
        case CASE:
        case CASEZ:
        case CASEX:
            case_statement(node, endToken);
            break;
            
        case FOREVER:
        case FOR:
        case WHILE:
            loop_statement(node, endToken);
            break;
            
        case REPEAT:
            endToken = findTokenInBlock(SEMICOLON, endToken);
            if(findTokenInBlock(AT, endToken) != null) {
                delay_or_event_statement(node, endToken);
            }else {
                loop_statement(node, endToken);
            }
            break;
            
        case WAIT:
            wait_statement(node, endToken);
            break;
            
        case HYPHEN_ARROW:
            event_trigger_statement(node, endToken);
            break;
            
        case BEGIN:
            seq_block(node, endToken);
            break;
            
        case FORK:
            par_block(node, endToken);
            break;
            
        case DISABLE:
            disable_task_or_block(node, endToken);
            break;
            
        case ASSIGN:
            assign_assignment(node, endToken);
            break;
            
        case DEASSIGN:
            deassign_lvalue(node, endToken);
            break;
            
        case FORCE:
            force_statement(node, endToken);
            break;
            
        case RELEASE:
            release_statement(node, endToken);
            break;
            
        case AT:
        case PARA:
            delay_or_event_statement(node, endToken);
            break;
            
        default:
            endToken = findTokenInBlock(SEMICOLON, endToken);
            endToken = tokenMgr.getNextToken(endToken);
            if(findTokenInBlock(LE, endToken) != null) {
                non_block_assignment_statement(node, endToken);
            }else if(findTokenInBlock(EQ, endToken) != null) {
                block_assignment_statement(node, endToken);
            }else if(tokenMgr.getNextToken().image.startsWith(tokenImage[DOLLAR])) {
                system_task_enable(node, endToken);
            }else {
                task_enable(node, endToken);
            }
            break;
        }

        closeNodeScope(node);
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
    }

    /**
     * statement_or_null <br>
     *     ::=  statement  <br>
     *     ||= ; 
     */
    void statement_or_null(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTATEMENT_OR_NULL);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() != SEMICOLON
                && tokenMgr.getNextToken() != endToken) {
            statement(node, endToken);
        }else if(tokenMgr.getNextTokenKind() == SEMICOLON) {
            consumeToken(SEMICOLON);
        }
        closeNodeScope(node);
    }

    /**
     * strength0  is one of the following keywords: <br>
     *     <b>supply0</b> <b>strong0</b> <b>pull0</b> <b>weak0</b> <b>highz0</b> 
     */
    void strength0(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTRENGTH0);
        openNodeScope(node);
        if(!is_strength0()) {
            throw new ParserException(tokenMgr.getNextToken());
        }
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }
    
    /**
     * strength1  is one of the following keywords: <br>
     *     <b>supply1</b> <b>strong1</b> <b>pull1</b> <b>weak1</b> <b>highz1</b> 
     */
    void strength1(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTRENGTH1);
        openNodeScope(node);
        if(!is_strength1()) {
            throw new ParserException(tokenMgr.getNextToken());
        }
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * string is text enclosed in "" and contained on one line.
     */
//    void string(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTSTRING);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * system_identifier <br>
     *     An  IDENTIFIER  assigned to an existing system <b>task</b> <b>or</b> <b>function</b> 
     */
    void system_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSYSTEM_IDENTIFIER);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }

    /**
     * system_task_enable <br>
     *     ::=  name_of_system_task  ; <br>
     *     ||=  name_of_system_task  (  expression  {, expression } ) ; 
     */
    void system_task_enable(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSYSTEM_TASK_ENABLE);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        Token tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
        if(tmpToken == null) {
            name_of_system_task(node, endToken);
        }else {
            name_of_system_task(node, tmpToken);
            consumeToken(LPARENTHESIS);
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
            consumeToken(RPARENTHESIS);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * system_timing_check <br>
     *     ::= $setup(  timing_check_event ,  timing_check_event , <br>
     *          timing_check_limit  <br>
     *         [, notify_register ] ) ; <br>
     *     ||= $hold(  timing_check_event ,  timing_check_event , <br>
     *          timing_check_limit  <br>
     *         [, notify_register ] ) ; <br>
     *     ||= $period(  controlled_timing_check_event ,  timing_check_limit  <br>
     *         [, notify_register ] ) ; <br>
     *     ||= $width(  controlled_timing_check_event ,  timing_check_limit  <br>
     *         [, constant_expression ,  notify_register ] ) ; <br>
     *     ||= $skew(  timing_check_event ,  timing_check_event , <br>
     *          timing_check_limit  <br>
     *         [, notify_register ] ) ; <br>
     *     ||= $recovery(  controlled_timing_check_event , <br>
     *          timing_check_event , <br>
     *          timing_check_limit  [, notify_register ] ) ; <br>
     *     ||= $setuphold(  timing_check_event ,  timing_check_event , <br>
     *          timing_check_limit ,  timing_check_limit  [, notify_register ] ) ; 
     */
    void system_timing_check(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSYSTEM_TIMING_CHECK);
        openNodeScope(node);
        String image = tokenMgr.getNextToken().image;
        Token tmpToken = null;
        
        new ASTtoken(node, image);
        tokenMgr.toNextToken();
        consumeToken(LPARENTHESIS);
        endToken = findTokenInBlock(RPARENTHESIS, endToken);
        
        if(image.equals(SYS_SETUP) || image.equals(SYS_HOLD)
                || image.equals(SYS_SKEW) || image.equals(SYS_SETUPHOLD)) {
            
            tmpToken = findTokenInBlock(COMMA, endToken);
            timing_check_event(node, tmpToken);
            consumeToken(COMMA);
            
            tmpToken = findTokenInBlock(COMMA, endToken);
            timing_check_event(node, tmpToken);
            consumeToken(COMMA);
            
            tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            timing_check_limit(node, tmpToken);
            
            if(image.equals(SYS_SETUPHOLD)) {
                consumeToken(COMMA);
                tmpToken = findTokenInBlock(COMMA, endToken);
                if(tmpToken == null)
                    tmpToken = endToken;
                timing_check_limit(node, tmpToken);
            }
        }else if(image.equals(SYS_PERIOD) || image.equals(SYS_RECOVERY)) {
            tmpToken = findTokenInBlock(COMMA, endToken);
            controlled_timing_check_event(node, tmpToken);
            consumeToken(COMMA);
            
            if(image.equals(SYS_RECOVERY)) {
                tmpToken = findTokenInBlock(COMMA, endToken);
                timing_check_event(node, tmpToken);
                consumeToken(COMMA);
            }
            
            tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            timing_check_limit(node, tmpToken);
            
        }else if(image.equals(SYS_WIDTH)) {
            tmpToken = findTokenInBlock(COMMA, endToken);
            controlled_timing_check_event(node, tmpToken);
            consumeToken(COMMA);
            
            tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            timing_check_limit(node, tmpToken);
            
            if(tokenMgr.getNextTokenKind() == COMMA) {
                consumeToken(COMMA);
                tmpToken = findTokenInBlock(COMMA, endToken);
                constant_expression(node, tmpToken);
            }
        }else {
            throw new ParserException(tokenMgr.getNextToken());
        }
        
        if(tokenMgr.getNextTokenKind() == COMMA) {
            consumeToken(COMMA);
            notify_register(node, endToken);
        }
        consumeToken(RPARENTHESIS);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * table_definition <br>
     *     ::= <b>table</b>  table_entries  <b>endtable</b> 
     */
    void table_definition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTABLE_DEFINITION);
        openNodeScope(node);
        consumeToken(TABLE);
        endToken = findTokenInBlock(ENDTABLE, endToken);
        table_entries(node, endToken);
        consumeToken(ENDTABLE);
        closeNodeScope(node);
    }

    /**
     * table_entries <br>
     *     ::= { combinational_entry }+ <br>
     *     ||= { sequential_entry }+ 
     */
    void table_entries(IASTNode p, Token endToken) throws ParserException {
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
        ASTNode node = new ASTNode(p, ASTTABLE_ENTRIES);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(COLON, endToken);
        Token tmpToken1 = findTokenInBlock(tokenMgr.getNextToken(tmpToken), COLON, endToken);
        if(tmpToken1 == null) {
            while(true) {
                combinational_entry(node, endToken);
                tmpToken = findTokenInBlock(SEMICOLON, endToken);
                tmpToken1 = findTokenInBlock(COLON, tmpToken);
                if(tmpToken1 == null) {
                    break;
                }
            }
        }else {
            while(true) {
                sequential_entry(node, endToken);
                tmpToken = findTokenInBlock(SEMICOLON, endToken);
                tmpToken1 = findTokenInBlock(COLON, tmpToken);
                if(tmpToken1 == null) {
                    break;
                }
            }
        }

        closeNodeScope(node);
        while(is_compiler_directives()) {
            compiler_directives(p, endToken);
        }
    }

    /**
     * <b>task</b> <br>
     *     ::= <b>task</b>  name_of_task  ; <br>
     *         { tf_declaration } <br>
     *          statement_or_null  <br>
     *         <b>endtask</b> 
     */
    void task(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTASK);
        openNodeScope(node);
        consumeToken(TASK);
        endToken = findTokenInBlock(ENDTASK, endToken);
        Token tmpToken = findTokenInBlock(SEMICOLON, endToken);
        name_of_task(node, tmpToken);
        startBlock();
        consumeToken(SEMICOLON);
        while(is_tf_declaration()) {
            tf_declaration(node, endToken);
        }
        statement_or_null(node, endToken);
        endBlock();
        consumeToken(ENDTASK);
        closeNodeScope(node);
        SymbolParser.parseSubProgramKind(node, this);
    }

    /**
     * task_enable <br>
     *     ::=  name_of_task  ;<br>
     *     ||=  name_of_task  (  expression  {, expression } ) ; 
     */
    void task_enable(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTASK_ENABLE);
        openNodeScope(node);
        Token tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
        if(tmpToken == null) {
            name_of_task(node, endToken);
        }else {
            name_of_task(node, tmpToken);
            endToken = findTokenInBlock(SEMICOLON, endToken);
            consumeToken(LPARENTHESIS);
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
            consumeToken(RPARENTHESIS);
        }
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * terminal <br>
     *     ::=  expression  <br>
     *     ||=  IDENTIFIER  
     */
    void terminal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTERMINAL);
        openNodeScope(node);
        expression(node, endToken); // include IDENTIFIER
        closeNodeScope(node);
    }

    /**
     * tf_declaration <br>
     *     ::=  parameter_declaration  <br>
     *     ||=  input_declaration  <br>
     *     ||=  output_declaration  <br>
     *     ||=  inout_declaration  <br>
     *     ||=  reg_declaration  <br>
     *     ||=  time_declaration  <br>
     *     ||=  integer_declaration  <br>
     *     ||=  real_declaration  
     */
    void tf_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTF_DECLARATION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        switch(kind)
        {
        case PARAMETER:
            parameter_declaration(node, endToken);
            break;
        case INPUT:
            input_declaration(node, endToken);
            break;
        case OUTPUT:
            output_declaration(node, endToken);
            break;
        case INOUT:
            inout_declaration(node, endToken);
            break;
        case REG:
            reg_declaration(node, endToken);
            break;
        case TIME:
            time_declaration(node, endToken);
            break;
        case INTEGER:
            integer_declaration(node, endToken);
            break;
        case REAL:
        default:
            real_declaration(node, endToken);
            break;
        }
        closeNodeScope(node);
    }

    /**
     * time_declaration <br>
     *     ::= <b>time</b>  list_of_register_variables  ; 
     */
    void time_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIME_DECLARATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        consumeToken(TIME);
        list_of_register_variables(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
        SymbolParser.parseVariableKind(node, this);
    }

    /**
     * timing_check_condition <br>
     *     ::=  scalar_timing_check_condition  <br>
     *     ||= (  scalar_timing_check_condition  ) 
     */
    void timing_check_condition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMING_CHECK_CONDITION);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            consumeToken(LPARENTHESIS);
            scalar_timing_check_condition(node, endToken);
            consumeToken(RPARENTHESIS);
        }else {
            scalar_timing_check_condition(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * timing_check_event <br>
     *     ::= [ timing_check_event_control ]  specify_terminal_descriptor  <br>
     *         [&&&  timing_check_condition ] 
     */
    void timing_check_event(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMING_CHECK_EVENT);
        openNodeScope(node);
        if(is_timing_check_event_control()) {
            timing_check_event_control(node, endToken);
        }
        specify_terminal_descriptor(node, endToken);
        if(tokenMgr.getNextTokenKind() == TRI_AND) {
            timing_check_condition(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * timing_check_event_control <br>
     *     ::= <b>posedge</b> <br>
     *     ||= <b>negedge</b> <br>
     *     ||=  edge_control_specifier  
     */
    void timing_check_event_control(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMING_CHECK_EVENT_CONTROL);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        if(kind == POSEDGE || kind == NEGEDGE) {
            new ASTtoken(node, tokenImage[kind]);
            tokenMgr.toNextToken();
        }else {
            edge_control_specifier(node, endToken);
        }
        closeNodeScope(node);
    }

    /**
     * timing_check_limit <br>
     *     ::=  expression  
     */
    void timing_check_limit(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMING_CHECK_LIMIT);
        openNodeScope(node);
        expression(node, endToken);
        closeNodeScope(node);
    }

    /**
     * udp <br>
     *     ::= <b>primitive</b>  name_of_udp  (  name_of_variable  <br>
     *         {, name_of_variable } ) ; <br>
     *         { udp_declaration }+ <br>
     *         [ udp_initial_statement ] <br>
     *          table_definition  <br>
     *         <b>endprimitive</b> 
     */
    void udp(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP);
        openNodeScope(node);
        consumeToken(PRIMITIVE);
        endToken = findTokenInBlock(ENDPRIMITIVE, endToken);
        
        Token tmpToken = findTokenInBlock(LPARENTHESIS, endToken);
        name_of_udp(node, tmpToken);
        String name = node.getLastChild().firstTokenImage();
        
        if(!parseSymbol) {
            startBlock();
            consumeToken(LPARENTHESIS);
            Token tmpToken1 = findTokenInBlock(RPARENTHESIS, endToken);
            while(true) {
                tmpToken = findTokenInBlock(COMMA, tmpToken1);
                if(tmpToken == null)
                    tmpToken = tmpToken1;
                terminal(node, tmpToken);
                if(tokenMgr.getNextTokenKind() != COMMA) {
                    break;
                }
                consumeToken(COMMA);
            }
            consumeToken(RPARENTHESIS);
            consumeToken(SEMICOLON);
            
            while(is_udp_declaration()) {
                udp_declaration(node, endToken);
            }
            
            if(tokenMgr.getNextTokenKind() == INITIAL) {
                udp_initial_statement(node, endToken);
            }
            
            table_definition(node, endToken);
            endBlock();
            
            consumeToken(ENDPRIMITIVE);
        }else {
            tokenMgr.setCurrentToken(endToken);
        }
        closeNodeScope(node);
        curSymbolTable.addSymbol(new Symbol(name, PRIMITIVE));
    }

    /**
     * udp_declaration <br>
     *     ::=  output_declaration  <br>
     *     ||=  reg_declaration  <br>
     *     ||=  input_declaration  
     */
    void udp_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_DECLARATION);
        openNodeScope(node);
        int kind = tokenMgr.getNextTokenKind();
        switch(kind)
        {
        case OUTPUT:
            output_declaration(node, endToken);
            break;
        case REG:
            reg_declaration(node, endToken);
            break;
        case INPUT:
        default:
            input_declaration(node, endToken);
            break;
        }
        closeNodeScope(node);
    }
    
    /**
     * udp_initial_statement <br>
     *     ::= <b>initial</b>  output_terminal_name  =  init_val  ; 
     */
    void udp_initial_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_INITIAL_STATEMENT);
        openNodeScope(node);
        consumeToken(INITIAL);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        Token tmpToken = findTokenInBlock(EQ, endToken);
        output_terminal_name(node, tmpToken);
        consumeToken(EQ);
        init_val(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }

    /**
     * udp_instance <br>
     *     ::= [ name_of_udp_instance ] (  terminal  {, terminal } ) 
     */
    void udp_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_INSTANCE);
        openNodeScope(node);
        if(tokenMgr.getNextTokenKind() != LPARENTHESIS) {
            name_of_udp_instance(node, endToken);
        }
        consumeToken(LPARENTHESIS);
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            terminal(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        consumeToken(RPARENTHESIS);
        closeNodeScope(node);
    }

    /**
     * udp_instantiation <br>
     *     ::=  name_of_udp  [ drive_strength ] [ delay ] <br>
     *      udp_instance  {, udp_instance } ; 
     */
    void udp_instantiation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_INSTANTIATION);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        
        name_of_udp(node, endToken);
        if(tokenMgr.getNextTokenKind() == LPARENTHESIS) {
            drive_strength(node, endToken);
        }
        if(is_delay()) {
            delay(node, endToken);
        }
        while(true) {
            Token tmpToken = findTokenInBlock(COMMA, endToken);
            if(tmpToken == null)
                tmpToken = endToken;
            udp_instance(node, tmpToken);
            if(tokenMgr.getNextTokenKind() != COMMA) {
                break;
            }
            consumeToken(COMMA);
        }
        closeNodeScope(node);
    }

    /**
     * unary_operator  is one of the following tokens: <br>
     *     + - ! ~ & ~& | ^| ^ ~^ 
     */
    void unary_operator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUNARY_OPERATOR);
        openNodeScope(node);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }
    
    /**
     * unsigned_number <br>
     *     ::= A number containing a set of any of the following characters: <br>
     *             0123456789_ 
     */
//    void unsigned_number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTUNSIGNED_NUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    // additional nodes(statements)
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  <b>if</b> (  expression  )  statement_or_null  <br>
     *  <b>if</b> (  expression  )  statement_or_null  <b>else</b>  statement_or_null
     */
    void if_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTIF_STATEMENT);
        openNodeScope(node);
        consumeToken(IF);
        consumeToken(LPARENTHESIS);
        Token tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
        expression(node, tmpToken);
        consumeToken(RPARENTHESIS);
        
        statement_or_null(node, endToken);
        if(tokenMgr.getNextTokenKind() == ELSE) {
            consumeToken(ELSE);
            
            statement_or_null(node, endToken);
        }
        closeNodeScope(node);
    }
    
    /**
     *  <b>case</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
     *  <b>casez</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
     *  <b>casex</b> (  expression  ) { case_item }+ <b>endcase</b>
     */
    void case_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCASE_STATEMENT);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        endToken = findTokenInBlock(ENDCASE, endToken);
        
        consumeToken(LPARENTHESIS);
        Token tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
        expression(node, tmpToken);
        consumeToken(RPARENTHESIS);
        
        while(tokenMgr.getNextTokenKind() != ENDCASE) {
            case_item(node, endToken);
        }
        
        consumeToken(ENDCASE);
        closeNodeScope(node);
    }
    
    /**
     *   <b>forever</b>  statement  <br>
     *   <b>repeat</b> (  expression  )  statement  <br>
     *   <b>while</b> (  expression  )  statement  <br>
     *   <b>for</b> (  assignment  ;  expression  ;  assignment  )  statement
     */
    void loop_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLOOP_STATEMENT);
        openNodeScope(node);
        Token tmpToken = null;
        int kind = tokenMgr.getNextTokenKind();
        new ASTtoken(node, tokenImage[kind]);
        consumeToken(kind);
        
        if(kind == FOREVER) {
            statement(node, endToken);
        }else if(kind == REPEAT || kind == WHILE) {
            consumeToken(LPARENTHESIS);
            tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            expression(node, tmpToken);
            consumeToken(RPARENTHESIS);
            statement(node, endToken);
        }else if(kind == FOR) {
            consumeToken(LPARENTHESIS);
            tmpToken = findTokenInBlock(SEMICOLON, endToken);
            assignment(node, tmpToken);
            consumeToken(SEMICOLON);
            tmpToken = findTokenInBlock(SEMICOLON, endToken);
            expression(node, tmpToken);
            consumeToken(SEMICOLON);
            tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
            assignment(node, tmpToken);
            
            consumeToken(RPARENTHESIS);
            statement(node, endToken);
        }else {
            throw new ParserException(tokenMgr.getNextToken());
        }

        closeNodeScope(node);
    }
    
    /**
     *   <b>wait</b> (  expression  )  statement_or_null
     */
    void wait_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTWAIT_STATEMENT);
        openNodeScope(node);
        consumeToken(WAIT);
        consumeToken(LPARENTHESIS);
        Token tmpToken = findTokenInBlock(RPARENTHESIS, endToken);
        expression(node, tmpToken);
        consumeToken(RPARENTHESIS);
        
        tmpToken = findTokenInBlock(ELSE, endToken);
        if(tmpToken == null)
            tmpToken = endToken;
        statement_or_null(node, tmpToken);
        closeNodeScope(node);
    }
    
    /**
     *   delay_or_event_control   statement_or_null
     */
    void delay_or_event_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY_OR_EVENT_STATEMENT);
        openNodeScope(node);
        delay_or_event_control(node, endToken);
        statement_or_null(node, endToken);
        closeNodeScope(node);
    }
    
    /**
     *   <b>disable</b>  name_of_task  ; <br>
     *   <b>disable</b>  name_of_block  ;
     */
    void disable_task_or_block(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDISABLE_TASK_OR_BLOCK);
        openNodeScope(node);
        consumeToken(DISABLE);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        name_of_task(node, endToken);   //TODO: check task/block name
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    /**
     *   <b>assign</b>  assignment  ;
     */
    void assign_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTASSIGN_ASSIGNMENT);
        openNodeScope(node);
        consumeToken(ASSIGN);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        assignment(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    /**
     *   <b>force</b>  assignment  ;
     */
    void force_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFORCE_ASSIGNMENT);
        openNodeScope(node);
        consumeToken(FORCE);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        assignment(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    /**
     *   <b>deassign</b>  lvalue  ;
     */
    void deassign_lvalue(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDEASSIGN_LVALUE);
        openNodeScope(node);
        consumeToken(DEASSIGN);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        lvalue(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    /**
     *   <b>release</b>  lvalue  ; 
     */
    void release_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRELEASE_LVALUE);
        openNodeScope(node);
        consumeToken(RELEASE);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        lvalue(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    /**
     *   ->  name_of_event  ;
     */
    void event_trigger_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEVENT_TRIGGER_STATEMENT);
        openNodeScope(node);
        consumeToken(HYPHEN_ARROW);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        name_of_event(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    /**
     *   blocking_assignment  ;
     */
    void block_assignment_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_ASSIGNMENT_STATEMENT);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        blocking_assignment(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    /**
     *   non_blocking_assignment  ;
     */
    void non_block_assignment_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNON_BLOCK_ASSIGNMENT_STATEMENT);
        openNodeScope(node);
        endToken = findTokenInBlock(SEMICOLON, endToken);
        non_blocking_assignment(node, endToken);
        consumeToken(SEMICOLON);
        closeNodeScope(node);
    }
    
    ////////////////////////////////////////////////////////
    // compiler directives
    ////////////////////////////////////////////////////////
    void compiler_directives(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMPILER_DIRECTIVE);
        openNodeScope(node);
        
        String image = tokenMgr.getNextToken().image;
        int index = -1;
        for(int i = 0; i < cdStrings.length; i++) {
            if(image.equals(cdStrings[i])) {
                index = i;
                break;
            }
        }
        
        if(index < 0)
            return;
        
        switch(index)
        {
        case CD_DEFAULT_NETTYPE:
            cd_default_nettype(node, endToken);
            break;
        case CD_DEFINE:
            cd_define(node, endToken);
            break;
        case CD_IFDEF:
            cd_ifdef(node, endToken);
            break;
        case CD_INCLUDE:
            cd_include(node, endToken);
            break;
        case CD_TIMESCALE:
            cd_timescale(node, endToken);
            break;
        default:
            new ASTtoken(node, image);
            tokenMgr.toNextToken();
            break;
        }
        closeNodeScope(node);
    }
    
    void cd_default_nettype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCD_DEFAULT_NETTYPE);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }
    
    void cd_define(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCD_DEFINE);
        openNodeScope(node);
        if(defineMap == null) {
            defineMap = new HashMap<String, ASTNode>();
        }
        
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        
        int line = tokenMgr.getCurrentToken().beginLine;
        String name = tokenMgr.getNextToken().image;
        new ASTtoken(node, name);
        tokenMgr.toNextToken();
        
        String value = "";
        while(tokenMgr.getNextToken().beginLine <= line) {
            value += tokenMgr.getNextToken().image;
            tokenMgr.toNextToken();
            value += " ";
        }
        new ASTtoken(node, value.trim());

        defineMap.put(name, node);
        closeNodeScope(node);
        
        Symbol sym = new Symbol(name, MACRO_DEFINE, value.trim());
        curSymbolTable.addSymbol(sym);
    }
    
    void cd_ifdef(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCD_IFDEF);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }
    
    //TODO: check whether the included file exist
    void cd_include(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCD_INCLUDE);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        
        //if(!libMgr.isTableExist(node.getLastChild().firstTokenImage())){
        //    throw new SymbolNotFoundException(tokenMgr.getCurrentToken());
        //}
        closeNodeScope(node);
    }
    
    void cd_timescale(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCD_TIMESCALE);
        openNodeScope(node);
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        if(tokenMgr.getNextTokenKind() != VerilogTokenConstants.DIV) {
            throw new ParserException(tokenMgr.getCurrentToken());
        }
        tokenMgr.toNextToken();
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        new ASTtoken(node, tokenMgr.getNextToken().image);
        tokenMgr.toNextToken();
        closeNodeScope(node);
    }
    
    
    public VerilogTokenManager getTokenManager() {
        return tokenMgr;
    }

    public ASTNode getMacroContent(String image) {
        if(defineMap == null) {
            return null;
        }
        return defineMap.get(image);
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
        return sourceText;
    }

    @Override
    public IASTNode parse(String path) throws ParserException {
        try {
            BufferedReader stream = new BufferedReader(new FileReader(path));
            tokenMgr = new VerilogTokenManager(stream, parseSymbol, this);
            sourceText = source_text(); 
            return sourceText;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public IASTNode parse(Reader reader) throws ParserException {
        BufferedReader stream = new BufferedReader(reader);
        tokenMgr = new VerilogTokenManager(stream, parseSymbol, this);
        sourceText = source_text(); 
        return sourceText;
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
}
