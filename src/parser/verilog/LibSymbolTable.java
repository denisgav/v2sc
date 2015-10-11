package parser.verilog;


import common.MyDebug;

import parser.ISymbol;

public class LibSymbolTable extends SymbolTable
{
    public LibSymbolTable(SymbolTable p, String name) {
        super(p, name);
        if(!libMgr.isTableExist(tabName)) {
            MyDebug.printFileLine("symbol table not exist:" + tabName + ", it may be a symbol");
        }
    }
    
    @Override
    public boolean addSymbol(ISymbol sym) {
        return false;    // forbid add symbol to library symbol table
    }
    
    @Override
    public ISymbol getSymbol(String name) {
        Symbol[] ret = libMgr.getSymbol(tabName, name);
        if(ret != null)
            return ret[0];
        if(parent != null)
            return parent.getSymbol(name);
        return null;
    }

    @Override
    public ISymbol[] getAllSymbols() {
        return libMgr.getSymbol(tabName, null); 
    }
    
    public SymbolTable getTableOfSymbol(String name) {
        if(libMgr.getSymbol(tabName, name) != null)
            return this;
        if(parent != null)
            return parent.getTableOfSymbol(name);
        return null;
    }
}
