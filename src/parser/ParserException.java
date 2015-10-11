package parser;

public class ParserException extends Exception {
    protected String message;
    protected int line;
    protected int column;

    private static final long serialVersionUID = 1L;
    
    public ParserException(){
        message = "";
        line = 0;
        column = 0;
    }
    
    public ParserException(Token tkn){
        super(tkn.dump());
        message = tkn.dump();
        line = tkn.beginLine;
        column = tkn.beginColumn;
    }
    
    public ParserException(Token tkn, String msg) {
        super(msg + System.lineSeparator() + tkn.dump());
        message = tkn.dump();
        line = tkn.beginLine;
        column = tkn.beginColumn;
    }
    
    public String getMessage(){
        return message;
    }
    
    public int getStartLine(){
        return line;
    }
    
    public int getColumn(){
        return column;
    }
}
