package parser.verilog;

import parser.ISymbol;

public class LocalSymbolTable extends SymbolTable
{
    VerilogArrayList<Symbol> mysyms = new VerilogArrayList<Symbol>();
    
    protected LocalSymbolTable(SymbolTable p, String name) {
        super(p, name);
        mysyms = new VerilogArrayList<Symbol>();
    }
    
    @Override
    public boolean addSymbol(ISymbol sym) {
        boolean ret = true;
        if(mysyms != null)
            ret = mysyms.add((Symbol)sym);
        return ret;
    }
    
    @Override
    public ISymbol getSymbol(String name) {
        Symbol[] ret = null;
        if(mysyms != null) {
            ret = mysyms.get(name);
            if(ret != null)
                return ret[0];
        }
        
        if(parent != null)
            return parent.getSymbol(name);
        return null;
    }

    @Override
    public ISymbol[] getAllSymbols() {
        return mysyms.toArray(new Symbol[mysyms.size()]); 
    }
    
    public SymbolTable getTableOfSymbol(String name) {
        if(mysyms.get(name) != null)
            return this;
        if(parent != null)
            return parent.getTableOfSymbol(name);
        return null;
    }
}
