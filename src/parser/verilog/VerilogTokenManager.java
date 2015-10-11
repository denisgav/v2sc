package parser.verilog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import common.MyDebug;

import parser.CommentBlock;
import parser.ParserException;
import parser.Token;
import parser.TokenManager;

/** verilog literal/identifier regular expression */
class RegExp
{
   /**
    * number ::=   decimal_number | octal_number | binary_number | hex_number | real_number
    * decimal_number ::=
    *      [ sign ] unsigned_number
    *    | [ size ] decimal_base unsigned_number
    
    * binary_number ::= [ size ] binary_base binary_digit { _ | binary_digit }
    
    * octal_number ::= [ size ] octal_base octal_digit { _ | octal_digit }
    
    * hex_number ::= [ size ] hex_base hex_digit { _ | hex_digit }
    
    * real_number ::=
    *       [sign] unsigned_number.unsigned_number
    *     | [sign]unsigned_number[.unsigned_number]e[sign]unsigned_number
    *     | [sign]unsigned_number[.unsigned_number]E[sign]unsigned_number
    
    * sign ::= + | -
    * size ::= unsigned_number
    * unsigned_number ::= decimal_digit { _ | decimal_digit }
    * decimal_base ::= 'd | 'D
    * binary_base ::= 'b | 'B
    * octal_base ::= 'o | 'O
    * hex_base ::= 'h | 'H
    * decimal_digit ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
    * binary_digit ::= x | X | z | Z | 0 | 1
    * octal_digit ::= x | X | z | Z | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7
    * hex_digit ::=   x | X | z | Z | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7| 8 | 9 | a | b | c | d | e | f | A | B | C | D | E | F
    */
    static final String _decimal_base = "'[dD]";
    static final String _binary_base = "'[bB]";
    static final String _octal_base = "'[oO]";
    static final String _hex_base = "'[hH]";
    static final String _decimal_digit = "[0-9]";
    static final String _binary_digit = "[xXzZ01]";
    static final String _octal_digit = "[xXzZ0-7]";
    static final String _hex_digit = "[xXzZ0-9a-fA-F]";
    
    static final String _sign = "[+-]";
    static final String _unsigned_number = _decimal_digit + "[_" + _decimal_digit + "]*";    
    static final String _size = _unsigned_number;
    
    static final String decimal_number = "(?:" + "((" + _sign + ")?" + _unsigned_number + ")|" 
                                + "((" + _size + ")?" + _decimal_base + _unsigned_number + ")"
                                + ")";
    static final String binary_number = "(" + _size + ")?" + _binary_base + _binary_digit
                                + "[_" + _binary_digit + "]*";
    
    static final String octal_number = "(" + _size + ")?" + _octal_base + _octal_digit
                                + "[_" + _octal_digit + "]*";
    
    static final String hex_number = "(" + _size + ")?" + _hex_base + _hex_digit
                                + "[_" + _hex_digit + "]*";
    
    static final String real_number_0 = "(" + _sign + ")?" + _unsigned_number + "." 
                                + _unsigned_number;
    static final String real_number_1 = "(" + _sign + ")?" + _unsigned_number + "(." 
                                + _unsigned_number + ")?[eE]" + "(" + _sign + ")?"
                                + _unsigned_number;
    
    static boolean is_decimal_number(String str) {
        return str.matches(decimal_number);
    }
    static boolean is_binary_number(String str) {
        return str.matches(binary_number);
    }
    static boolean is_octal_number(String str) {
        return str.matches(octal_number);
    }
    static boolean is_hex_number(String str) {
        return str.matches(hex_number);
    }
    static boolean is_real_number(String str) {
        return str.matches(real_number_0) || str.matches(real_number_1);
    }
    
    static boolean is_number(String str) {
        return is_decimal_number(str) || is_binary_number(str)
               || is_octal_number(str) || is_hex_number(str)
               || is_real_number(str);
    }
    
    /**
     * IDENTIFIER := simple_identifier | escaped_identifier
     * simple_identifier ::= [a-zA-Z_]{[a-zA-Z_$0-9]}
     * escaped_identifier ::= \ {Any_ASCII_character_except_white_space} white_space
     */
    static final String _simple_identifier = "[`a-zA-Z_][`a-zA-Z_$0-9]*";
    static final String _escaped_identifier = "\\" + "[A-Za-z0-9" + " \r\n\t\b"
                                        + "`~!@#$%\\^&*\\(\\)_+|=\\-"
                                        + ",\\.:<>?\";\'/]*";
    static boolean is_IDENTIFIER(String str) {
        return str.matches(_simple_identifier) || str.matches(_escaped_identifier);
    }
}

public class VerilogTokenManager extends TokenManager implements VerilogTokenConstants
{
    VerilogParser parser = null;
    public VerilogTokenManager(BufferedReader stream, boolean parseSymbol, 
                    VerilogParser parser)
    {
        super(stream, parseSymbol);
        this.parser = parser;
    }
    
    public static final String specialChar = "&()*+,-./:;<>=|[]!%@?~^{}#";
    static final char singleQuote = '\'';
    static final char doubleQuote = '\"';
    static final char backSlash = '\\';
    
    /**
     * skip space and tabulation and comment
     * @return true if success, false if end of file
     */
    protected boolean skipInvalid() throws IOException
    {
        if(strLine == null)
            return false;
        
        boolean ret = false;
        int emtyLines = 0;
        CommentBlock cb = null;
        out:
        while(true) {
            // read until not empty line
            while(column >= strLine.length()) {
                strLine = stream.readLine();
                if(strLine == null)
                    break out;    // end of file
                column = 0;
                line ++;
                if(line > 1)
                    emtyLines ++;
            }
            
            // check comment
            if(column < (strLine.length() - 1) && strLine.charAt(column) == '/'
                    && (strLine.charAt(column+1) == '/' || strLine.charAt(column+1) == '*')) {
                if(strLine.charAt(column+1) == '/') {
                    // line comment
                    if(!parseSymbol) {
                        if(cb == null) {
                            cb = new CommentBlock(line);
                        }
                        
                        //for(int i =0; i < emtyLines; i++) {
                        if(emtyLines > 0) {
                            cb.commentLines.add("");    // add empty lines as comment
                        }
                        emtyLines = 0;
                        cb.commentLines.add(strLine.substring(column));
                    }
                    strLine = stream.readLine();
                    if(strLine == null)
                        break out;    // read end of file
                    column = -1;
                    line ++;
                }else {
                    // block comment
                    while(true) {
                        if(!parseSymbol) {
                            if(cb == null) {
                                cb = new CommentBlock(line);
                            }
                            
                            emtyLines = 0;
                            cb.commentLines.add(strLine.substring(column));
                        }
                        
                        column = strLine.indexOf("*/");
                        if(column >= 0) {
                            column += 2;    // skip comment end
                            break;
                        }
                        
                        strLine = stream.readLine();
                        if(strLine == null)
                            break out;    // read end of file
                        column = 0;
                        line ++;
                    }
                }
            }else if(lastToken != null && lastToken.kind == SEMICOLON
                    && strLine.charAt(column) == ';') {
                continue;   // ignore several continuous semicolon
            }else if(!Character.isWhitespace(strLine.charAt(column))) {
                ret = true;
                break;      // break when not white space character
            }

            column ++;
        }

        if(cb != null) {
            //for(int i =0; i < emtyLines; i++) {
            if(emtyLines > 0) {
                cb.commentLines.add("");    // add empty lines as comment
            }
            cb.endLine = line - 1;
            comments.add(cb);
        }
        return ret;
    }
   
    /**
     * read next token image string in current line
     * @return null if reach end of file and no any valid string
     */
    protected String getNextImage() throws ParserException
    {
        String ret = "";
        char lastChar = 0;
        char c;
        boolean error = false;
        boolean first = true;
        int curColumn = column + 1;
        
        int max = strLine.length();
        while(column < max && !error)     // one token always in one line
        {
            c = strLine.charAt(column);
            if(Character.isWhitespace(c)) {
                if(!ret.isEmpty() && ret.indexOf(singleQuote) > 0
                        && !RegExp.is_number(ret)) {
                    column ++;
                    continue;
                }else {
                    break;
                }
            }
            
            // escape identifier
            if(backSlash == c) {
                // while until whitespace or line feed or return
                while(column < max && !Character.isWhitespace(c)) {
                    ret += strLine.charAt(column);
                    column ++;
                }
                break;
            }
            
            
            if(!first &&((specialChar.indexOf(lastChar) < 0 && specialChar.indexOf(c) >= 0)
                    || (specialChar.indexOf(c) < 0 && specialChar.indexOf(lastChar) >= 0))) {
                if(column >= max) { break; }
                if(Character.isDigit(lastChar) && c == '.' 
                    && Character.isDigit(strLine.charAt(column+1))) {
                    // float point
                    ret += c;
                    column ++;
                    c = strLine.charAt(column);
                }else {
                    break;  // exit loop when character change between specialChar and letter&digit
                }
            }
            
            if(!first && Character.isDigit(lastChar) && !Character.isDigit(c)
                    && c != '.' && c != singleQuote && !ret.startsWith("`")) {
                String tmp = ret + c;
                // divide some string(such as 10ns) into two token
                if(!RegExp.is_IDENTIFIER(tmp) && !RegExp.is_number(tmp))
                    break;
            }
            
            // allow continuous specialChar
            if(!first && specialChar.indexOf(c) >= 0 && specialChar.indexOf(lastChar) >= 0)
            {
                // &&&
                // ===, !==
                // >=, <=, ==, !=
                // ~&, && 
                // ~|, ||
                // ^~, ~^
                // <<, >>
                // =>, *>, ->
                if((column < max-2) && (c == '&') && (strLine.charAt(column+1) == '&')
                    && (lastChar == '&')) {
                    ret += c;
                    ret += strLine.charAt(column+1);
                    column += 2;
                }else if((column < max-2) && (c == '=') && (strLine.charAt(column+1) == '=') 
                                  && ((lastChar == '!') || (lastChar == '='))) {
                    ret += c;
                    ret += strLine.charAt(column+1);
                    column += 2;
                }else if((c == '=' && (lastChar=='!' || lastChar=='>' 
                                    || lastChar=='<' || lastChar=='='))
                        || (c == '&' && (lastChar=='~' || lastChar=='&'))
                        || (c == '|' && (lastChar=='~' || lastChar=='|'))
                        || (c == '^' && lastChar=='~')
                        || (c == '~' && lastChar=='^')
                        || (c == '>' && lastChar=='>')
                        || (c == '<' && lastChar=='<')
                        || (c == '>' && (lastChar=='=' || lastChar=='*' || lastChar=='-'))
                        ) {
                    ret += c;
                    column ++;
                }
                
                break;
            }
            
            // string
            if(doubleQuote == c) {
                ret += c;
                column ++;
                while(column < max) {
                    char c1 = strLine.charAt(column);
                    ret += c1;
                    if(c1 == doubleQuote) {
                        column ++;
                        if(column >= max) {
                            break;
                        }
                        c1 = strLine.charAt(column);
                        if(c1 != doubleQuote) {
                            break;      // double quote in a string must a pair put together
                        }
                        ret += c1;
                    }
                    column ++;
                }
                
                if(column >= max) {
                    //error = true;   //TODO can double quote be next line?
                }
                break;  // always quit loop on double quote
            }
            
            first = false;
            ret += c;
            lastChar = c;
            column ++;
        }
        
        if(error) {
            throw new ParserException(newOneToken(ret, curColumn));
        }

        if(ret.isEmpty())
            return null;
        
        // check macro define
        if(parser != null && ret.startsWith("`")) {
            int index = -1;
            for(int i = 0; i < CompilerDirectives.cdStrings.length; i++) {
                if(ret.equals(CompilerDirectives.cdStrings[i])) {
                    index = i;
                    break;
                }
            }
            
            if(index < 0) {
                // extract the content of macro
                ret = ret.substring(1);
                ASTNode node = parser.getMacroContent(ret);
                if(node == null) {
                    throw new SymbolNotFoundException(newOneToken(ret, curColumn));
                }
                
                // the first token is `define
                // the second token is macro name
                // content start from the third token
                Token token = node.getFirstToken().next.next;
                Token lToken = node.getLastToken();
                Token prev = null;
                if(token != lToken) {
                    while(token != null) {
                        Token newToken = newOneToken(token.image, curColumn);
                        newToken.endColumn = column;
                        newToken.kind = token.kind;
                        newToken.prev = prev;
                        newToken.special = true;
                        if(prev != null)
                            prev.next = newToken;
                        //lastToken = newToken;
                        if(token == lToken)
                            break;
                        
                        token = token.next;
                    }
                    ret = null;
                }else {
                    // only one token in the macro content, just return it
                    ret = token.image;
                }
            }
        }
        
        return ret;
    }
    
    private Token newOneToken(String image, int column) {
        Token token = new Token();
        token.image = image;
        token.beginLine = line;
        token.beginColumn = column;
        token.endLine = line;
        token.endColumn = column;
        token.kind = -1;
        token.next = null;
        return token;
    }
    
    /**
     * get buildin token id
     * @param image
     * @return token id, -1 if not found
     */
    protected int getBuildinTokenKind(String image)
    {
        int kind = -1;        
        for(int i = 0; i < tokenImage.length; i++) {
            if(image.equals(tokenImage[i])) {
                kind = i;
                break;
            }
        }
        return kind;
    }
    
    /**
     * get identifier/literal token id<br>
     * @param image
     * @return token id, -1 if not found
     */
    protected int getOtherTokenKind(String image)
    {
        if(RegExp.is_IDENTIFIER(image)) {
            return IDENTIFIER;
        }else if(RegExp.is_number(image)){
            return number_lexical;
        }else if(image.startsWith("\"") && image.endsWith("\"")){
            return string_lexical;
        }else if(image.startsWith("`")){
            return compiler_directive;
        }else{
            return -1;
        }
    }
    
    /** test TokenManager */
    public static void main(String[] argv)
    {
        try {
            String dir = System.getProperty("user.dir");
            TokenManager tm = new VerilogTokenManager(
                    new BufferedReader(
                            new FileReader(dir + "\\ac97_top.v")), false, null);
            Token token = null;
            int kind = tm.getNextTokenKind();
            kind = tm.getNextTokenKind(2);
            kind = tm.getNextTokenKind(5);
            //MyDebug.printFileLine(kind);
            
            int lastLine = 0x0fffffff;
            while(true) {
                token = tm.toNextToken();
                if(token == null) {
                    break;
                }
                if(lastLine < token.beginLine) {
                    MyDebug.printFileLine();
                }
                MyDebug.printFileLine(" " + token.image);
                lastLine = token.endLine;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

