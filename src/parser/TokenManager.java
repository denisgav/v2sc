package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import parser.CommentBlock;
import parser.ParserException;
import parser.Token;

public abstract class TokenManager
{
    protected BufferedReader stream = null;
    protected int line, column;
    protected String strLine;
    protected Token firstToken = null;  // first token
    protected Token lastToken = null;   // last scan token
    protected Token curToken = null;    // current processing token
    // saved current token(used to restore when scan to next several token)
    protected Token savedToken = null;
    
    // comment
    protected ArrayList<CommentBlock> comments = new ArrayList<CommentBlock>();
    // only parse symbol, don't care comments
    // save memory when parse library
    protected boolean parseSymbol = false;
    
    public TokenManager(BufferedReader stream, boolean parseSymbol)
    {
        this.stream = stream;
        line = column = 0;
        strLine = "";
        this.parseSymbol = parseSymbol;
    }
    
    public static final String specialChar = "&()*+,-./:;<>=|[]!$%@?~^{}";
    static final char singleQuote = '\'';
    static final char doubleQuote = '\"';
    static final char backSlash = '\\';
    
    /**
     * skip space and tabulation and comment
     * @return true if success, false if end of file
     */
    protected abstract boolean skipInvalid() throws IOException;
   
    /**
     * read next token image string in current line
     * @return null if reach end of file and no any valid string
     */
    protected abstract String getNextImage() throws ParserException;
   
    /**
     * get buildin token id
     * @param image
     * @return token id, -1 if not found
     */
    protected abstract int getBuildinTokenKind(String image);
    
    /**
     * get identifier/literal token id<br>
     * @param image
     * @return token id, -1 if not found
     */
    protected abstract int getOtherTokenKind(String image);
    
    /**
     * get token kind of specified image
     */
    protected int getTokenKind(String image)
    {
        if(image == null || image.isEmpty())
            return-1;
        int kind = getBuildinTokenKind(image);
        if(kind >= 0)
            return kind;
        
        return getOtherTokenKind(image);
    }
    
    /**
     * get current token
     */
    public Token getCurrentToken() throws ParserException
    {
        if(curToken == null)
        {
            getNextTokenKind();
            return firstToken;
        }
        else
        {
            return curToken;
        }
    }
    
    /**
     * set current token
     */
    public void setCurrentToken(Token token) throws ParserException
    {
        curToken = token;
    }
    
    /**
     * get kind of next token from current token
     */
    public int getNextTokenKind() throws ParserException
    {
        save();
        Token token = toNextToken();
        restore();
        if(token != null)
            return token.kind;
        else
            return -1;
    }
    
    /**
     * get kind of next token from specified token
     */
    public int getNextTokenKind(Token from) throws ParserException
    {
        save();
        curToken = from;
        Token token = toNextToken();
        restore();
        if(token != null)
            return token.kind;
        else
            return -1;
    }
    
    /**
     * get kind of next several number token from current token
     */
    public int getNextTokenKind(int nextNum) throws ParserException
    {
        Token token = getNextToken(nextNum);
        if(token != null)
            return token.kind;
        else
            return -1;
    }
    
    /**
     * get next several number token from current token
     */
    public Token getNextToken(int nextNum) throws ParserException
    {
        save();
        Token token = null;
        if(nextNum <= 0)
            return null;
        for(int i = 0; i < nextNum; i++)
            token = toNextToken();

        restore();
        return token;
    }
    
    /**
     * get kind of next several number token from specified token
     */
    public int getNextTokenKind(Token from, int nextNum) throws ParserException
    {
        Token token = getNextToken(from, nextNum);
        if(token != null)
            return token.kind;
        else
            return -1;
    }
    
    /**
     * get next several number token from specified token
     */
    public Token getNextToken(Token from, int nextNum) throws ParserException
    {
        save();
        Token token = null;
        curToken = from;
        if(nextNum <= 0)
            return null;
        for(int i = 0; i < nextNum; i++)
            token = toNextToken();
        
        restore();
        return token;
    }

    /**
     * scan next token from current token
     */
    public Token getNextToken() throws ParserException
    {
        save();
        Token ret = toNextToken();
        restore();
        return ret;
    }
    
    /**
     * scan next token from specified token
     */
    public Token getNextToken(Token from) throws ParserException
    {
        save();
        curToken = from;
        Token ret = toNextToken();
        restore();
        return ret;
    }
    
    /**
     * go to next token<br>
     * return null if reach the end of file or null stream<br>
     * throw exception if meet invalid character
     */
    public Token toNextToken() throws ParserException
    {
        if(stream == null)
            return null;
        
        if(curToken != lastToken) {
            if(curToken == null) {
                curToken = firstToken;
            }else {
                assert(curToken.next != null);
                curToken = curToken.next;
            }
            return curToken;
        }
        
        try {
            if(!skipInvalid())
            return null;
        }catch (IOException e) {
            throw new ParserException(curToken, "File Read Error!");
        }
        
        // save current line and column
        int curLine = line;
        int curColumn = column + 1;
        String image = getNextImage();
        if(image == null) {
            if(curToken != lastToken) {
                // macro has been extracted into several(more than one) token
                if(curToken == null) {
                    curToken = firstToken;
                }else {
                    assert(curToken.next != null);
                    curToken = curToken.next;
                }
                return curToken;
            }else {
                return null;
            }
        }
        
        // fill token member
        Token newToken = new Token();
        newToken.image = image;
        newToken.beginLine = curLine;
        newToken.beginColumn = curColumn;
        newToken.endLine = line;
        newToken.endColumn = column;
        newToken.kind = getTokenKind(image);
        newToken.next = null;

        if(curToken != null)
            curToken.next = newToken;
        newToken.prev = curToken;
        curToken = newToken;
        lastToken = curToken;
        if(firstToken == null) {
            firstToken = curToken;
        }
        return curToken;
    }
    
    /**
     * go to the first token<br>
     * throw exception if meet invalid character
     */
    public Token toFirstToken() throws ParserException
    {
        curToken = firstToken;
        return curToken;
    }
    
    /**
     * go to the last parsered token(may not be the last token of file)<br>
     * throw exception if meet invalid character
     */
    public Token toLastToken() throws ParserException
    {
        curToken = lastToken;
        return curToken;
    }
    
    /**
     * save current state
     */
    protected void save()
    {
        savedToken = curToken;
    }
    
    /**
     * restore to last state, must use with save()
     */
    protected void restore()
    {
        curToken = savedToken;
    }
    
    public CommentBlock[] getComment() {
        if(comments != null)
            return comments.toArray(new CommentBlock[comments.size()]);
        else
            return null;
    }
}

