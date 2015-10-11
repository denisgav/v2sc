package parser.verilog;

import parser.ParserException;
import parser.Token;

public class SymbolNotFoundException extends ParserException {
    private static final long serialVersionUID = -7341290364411176354L;
    
    public SymbolNotFoundException(Token tkn){
        super(tkn, "symbol not found");
    }
}
