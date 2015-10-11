package converter.vhdl;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


class ScCommonIdentifier extends ScVhdl {
    String identifier = "";
    String param = null;    // just used for statements in generate statement
    
    public ScCommonIdentifier(ASTNode node) {
        super(node);
    }
    
    public ScCommonIdentifier(ASTNode node, boolean needComment) {
        super(node, needComment);
    }
    
    public void setIdentifier(String ident) {
        identifier = ident;
    }
    
    public int getBitWidth() {
        Symbol sym = (Symbol)parser.getSymbol(curNode, identifier);
        if(sym != null) {
            String[] range = sym.typeRange;
            if(range != null) {
                return getWidth(sym.typeRange[0], sym.typeRange[2]);
            }
        }
        return 1;
    }
    
    public void setParam(String pa) {
        param = pa;
    }
    
    public String scString() {
        return identifier;
    }
}
