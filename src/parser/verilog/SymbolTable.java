package parser.verilog;

import java.util.ArrayList;
import java.util.StringTokenizer;

import parser.INameObject;
import parser.ISymbol;
import parser.ISymbolTable;

public abstract class SymbolTable implements ISymbolTable, INameObject
{
    protected String name = "";
    protected SymbolTable parent = null;
    protected String tabName = "tabName";
    
    protected VerilogArrayList<SymbolTable> children = null;
    
    protected static LibraryManager libMgr = LibraryManager.getInstance();
    
    protected SymbolTable(SymbolTable p, String name) {
        parent = p;
        this.name = name;
        tabName = getTableName();
        
        children = new VerilogArrayList<SymbolTable>();
        if(p != null) {
            p.children.add(this);
        }
    }
    

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String n) {
        name = n;
        tabName = getTableName();
    }
    
    @Override
    public boolean equals(INameObject other) {
        if(other == null || !(other instanceof SymbolTable))
            return false;
        return name.equalsIgnoreCase(other.getName());
    }
    
    public void setParent(SymbolTable p) {
        parent = p;
        if(p != null) {
            p.children.add(this);
            tabName = p.tabName + "#" + name;
            for(int i = 0; i < children.size(); i++)
                children.get(i).setParent(this);    // update children's table name
        }
    }
    
    public SymbolTable getParent() {
        return parent;
    }
    
    public String getTableName() {
        String ret = name;
        SymbolTable p = parent;
        while(p != null) {
            ret = p.name + "#" + ret;
            p = p.parent;
        }
        return ret;
    }
    
    public boolean addAll(SymbolTable other) {
        if(other == null)
            return false;
        ISymbol[] syms = other.getAllSymbols();
        if(syms == null)
            return false;
        for(int i = 0; i < syms.length; i++) {
            addSymbol(syms[i]);
        }
        children.addAll(other.children);
        return true;
    }
    
    /**
     * static method, get symbol by specified table name,
     * @param curTab current table
     * @param tableName table name, must be format like: a#b#c
     * @param name symbol name
     */
    public static Symbol getSymbol(SymbolTable curTab, String tableName, String name) {
        Symbol[] ret = libMgr.getSymbol(tableName, name);
        if(ret != null)
            return ret[0];
        
        if(curTab == null)
            return null;
        
        SymbolTable p = curTab;
        SymbolTable p1 = p;
        while(p1 != null) { // forward to root
            p = p1;
            p1 = p1.parent;
        }
        
        StringTokenizer tkn = new StringTokenizer(tableName, "#");
        tkn.nextToken();
        while(tkn.hasMoreTokens()) {
            String n = tkn.nextToken();
            SymbolTable[] temp = p.children.get(n);
            if(temp == null) {
                return null;
            }
            p = temp[0];
        }
        return (Symbol)p.getSymbol(name);
    }
    
    /**
     * static method, check whether symbol table exists
     */
    public static boolean isTableExist(SymbolTable curTab, String tabName) {
        if(libMgr.isTableExist(tabName))
            return true;

        if(curTab == null)
            return false;
        
        return (curTab.tabName.indexOf(tabName) >= 0);
    }
    
    public Symbol[] getKindSymbols(int kind) {
        ArrayList<Symbol> symArray = new ArrayList<Symbol>();

        Symbol[] syms = (Symbol[])getAllSymbols();
        if(syms == null) {
            return null;
        }
        for(int i = 0; i < syms.length; i++) {
            if(syms[i].kind == kind) {
                symArray.add(syms[i]);
            }
        }

        if(symArray.size() == 0) {
            return null;
        }else {
            return symArray.toArray(new Symbol[symArray.size()]);
        }
    }
    
    public SymbolTable getChildTable(String name) {
        for(int i = 0; i < children.size(); i++) {
            if(children.get(i).getName().equalsIgnoreCase(name)) {
                return children.get(i);
            }
        }
        return null;
    }
    
    public int getSymbolNum() {
        ISymbol[] syms = getAllSymbols();
        if(syms == null)
            return 0;
        else
            return syms.length;
    }
   
    public abstract SymbolTable getTableOfSymbol(String name);
    
    @Override
    public String toString() {
        return tabName;
    }
}
