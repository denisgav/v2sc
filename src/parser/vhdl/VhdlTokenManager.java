package parser.vhdl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import parser.CommentBlock;
import parser.ParserException;
import parser.Token;
import parser.TokenManager;

/** vhdl literal/identifier regular expression */
class RegExp
{
    /**
     * <pre>decimal_literal ::= integer [ . integer ] [ exponent ]
     * integer ::= digit { [ underline ] digit }
     * exponent ::= E [ + ] integer | E - integer
     */
    static final String _integer = "[0-9][[_]0-9]*";
    static final String _real = _integer + "(\\." + _integer + ")?";
    static final String _exponent = "[eE]([+-])?" + _integer;
    static final String regEx_decimal_literal = _real + "(" + _exponent + ")?";
    
    /**
     * <pre>based_literal ::= base # based_integer [ . based_integer ] # [ exponent ]
     * base ::= integer
     * based_integer ::= extended_digit { [ underline ] extended_digit }
     * extended_digit ::= digit | letter
     */
    static final String _base = _integer;
    static final String _extended_digit = "[0-9A-Fa-f]*";
    static final String _base_integer = _extended_digit + "([_]" + _extended_digit + ")*";
    static final String regEx_based_literal = _base + "#" + _base_integer + "(\\." + _base_integer + ")?" + 
                                        "#(" + _exponent + ")?";
    
    /**
     * <pre>identifier ::= basic_identifier | extended_identifier
     * basic_identifier ::= letter { [ underline ] letter_or_digit }
     * extended_identifier ::= \ graphic_character { graphic_character } \
     */
    static final String _base_identifier= "[A-Za-z][[_]A-Za-z0-9]*";
    static final String _graphic_character = "A-Za-z0-9" + " \r\n\t\b"
                                            + "`~!@#$%\\^&*\\(\\)_+|=\\-"
                                            + ",\\.:<>?\";\'/";
    static final String _extended_identifier= "\\\\([" + _graphic_character + "]([\\\\]{2})?)+\\\\";
    static final String regEx_identifier = "(?:(" + _base_identifier + ")|(" + _extended_identifier + "))";
    
    
    static final String _base_specifier = "[[Bb][Oo][Xx]]";
    static final String _bit_value = _base_integer;
    static final String regEx_bit_string_literal = _base_specifier + "\"" + _bit_value + "\"";
    
    static final boolean is_decimal_literal(String str) {
        return str.matches(regEx_decimal_literal);
    }
    
    static final boolean is_based_literal(String str) {
        return str.matches(regEx_based_literal);
    }
    
    static final boolean is_identifier(String str) {
        return str.matches(regEx_identifier);
    }
    
    static final boolean is_bit_string_literal(String str) {
        return str.matches(regEx_bit_string_literal);
    }
}

public class VhdlTokenManager extends TokenManager implements VhdlTokenConstants
{
    public VhdlTokenManager(BufferedReader stream, boolean parseSymbol)
    {
        super(stream, parseSymbol);
    }
    
    public static final String specialChar = "&()*+,-./:;<>=|[]!$%@?";
    static final char singleQuote = '\'';
    static final char doubleQuote = '\"';
    
    ArrayList<String> constArray = new ArrayList<String>();
    
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
            if(column < (strLine.length() - 1) && strLine.charAt(column) == '-'
                    && strLine.charAt(column+1) == '-') {
                if(!parseSymbol) {
                    if(cb == null) {
                        cb = new CommentBlock(line);
                    }
                    
                    //for(int i =0; i < emtyLines; i++) {
                    if(emtyLines > 0) {
                        cb.commentLines.add("");    // add empty lines as comment
                    }
                    emtyLines = 0;
                    cb.commentLines.add("//" + strLine.substring(column+2));
                }
                strLine = stream.readLine();
                if(strLine == null)
                    break out;    // read end of file
                column = -1;
                line ++;
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
            for(int i =0; i < emtyLines; i++) {
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
            if(Character.isWhitespace(c))
                break;
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
            
            // allow two continuous specialChar
            if(!first && specialChar.indexOf(c) >= 0 && specialChar.indexOf(lastChar) >= 0)
            {
                // **   
                // /=, >=, <=, ==, :=
                // <>, =>
                if((c == '*' && lastChar == '*')             
                    || (c == '=' && (lastChar=='/' || lastChar=='>' 
                                    || lastChar=='<' || lastChar=='=' 
                                    || lastChar==':'))
                    || (c == '>' && (lastChar=='<' || lastChar=='='))) {
                    ret += c;
                    column ++;
                }
                
                break;
            }
            
            if(singleQuote == c){
                if(Character.isLetterOrDigit(lastChar)) {
                    break;   // break when not character literal
                }
                column ++;
                if(column < max - 1) {
                    char c1 = strLine.charAt(column);
                    char c2 = strLine.charAt(column + 1);
                    ret += c;
                    if(singleQuote == c2) {
                        ret += c1;   // character literal
                        ret += c2;
                        column += 2;
                    }
                }else{
                    error = true;
                }
                break; // always quit loop on single quote
            }
            
            if(doubleQuote == c) {
                if(Character.isLetterOrDigit(lastChar) && !ret.matches(RegExp._base_specifier)) {
                    break;
                }
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
            Token token = new Token();
            token.image = ret;
            token.beginLine = line;
            token.beginColumn = curColumn;
            token.endLine = line;
            token.endColumn = column;
            token.kind = -1;
            token.next = null;
            throw new ParserException(token);
        }

        if(ret.isEmpty())
            return null;
        if(curToken != null && curToken.kind == CONSTANT) {
            constArray.add(ret);
        }else if(RegExp.is_identifier(ret)) {
            int i = 0;
            for(i = 0; i < constArray.size(); i++) {
                if(ret.equalsIgnoreCase(constArray.get(i))) {
                    ret = constArray.get(i);
                    break;
                }
            }
            
            if(i >= constArray.size()) {
                ret = ret.toLowerCase();
            }
        }
        return ret;
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
            if(image.equalsIgnoreCase(tokenImage[i])) {
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
        if(RegExp.is_identifier(image)) {
            return identifier;
        }else if(RegExp.is_decimal_literal(image)){
            return decimal_literal;
        }else if(RegExp.is_based_literal(image)){
            return based_literal;
        }else if(RegExp.is_bit_string_literal(image)){
            return bit_string_literal;
        }else if(image.length() == 3 && image.startsWith("\'") && image.endsWith("\'")){
            return character_literal;
        }else if(image.startsWith("\"") && image.endsWith("\"")){
            return string_literal;
        }else{
            return -1;
        }
    }
    
    /** test TokenManager */
    public static void main(String[] argv)
    {
        try {
            String dir = System.getProperty("user.dir");
            TokenManager tm = new VhdlTokenManager(
                    new BufferedReader(
                            new FileReader(dir + "\\ahbctrl.vhd")), false);
            Token token = null;
            int kind = tm.getNextTokenKind();
            kind = tm.getNextTokenKind(2);
            kind = tm.getNextTokenKind(5);
            System.out.print(kind);
            
            int lastLine = 0x0fffffff;
            while(true) {
                token = tm.toNextToken();
                if(token == null) {
                    break;
                }
                if(lastLine < token.beginLine) {
                    System.out.println();
                }
                System.out.print(" " + token.image);
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

