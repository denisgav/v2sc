package parser.verilog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import parser.IParser;

import common.FileList;
import common.MyDebug;

/**
 * all libraries
 */
public class LibraryManager
{
    static LibraryManager libMgr = null;
    static HashMap<String, ArrayList<Symbol>> symbolMap = 
                                new HashMap<String, ArrayList<Symbol>>();

    public static LibraryManager getInstance() {
        if(libMgr == null) {
            libMgr = new LibraryManager();
            //libMgr.deleteAll();
            
            //TODO: uncomment it 
            //libMgr.loadAll();
        }
        return libMgr;
    }
    
    protected void finalize() {
        if(libMgr != null) {
            libMgr.writeAll();
        }
    }
    
    private String getFileName(String path) {
        File file = new File(path);
        String ret = file.getName();
        file = null;
        return ret;
    }
    
    private boolean addChildren(String tabName, SymbolTable tab) {
        if(tabName == null || tabName.isEmpty() || tab == null 
                || tab.children == null) {
            return false;
        }
        
        for(int i = 0; i < tab.children.size(); i++) {
            SymbolTable tab1 = tab.children.get(i);
            String tabName1 = tabName + "#" + tab1.getName();
            addTable(tabName1, tab1);
        }
        return true;
    }
    
    /**
     * add symbol in table and it's children table
     */
    private boolean addTable(String tabName, SymbolTable tab) {
        ArrayList<Symbol> symsArray = symbolMap.get(tabName);
        if(symsArray == null) {
            symsArray = new ArrayList<Symbol>();
            symbolMap.put(tabName, symsArray);
        }else{
            MyDebug.printFileLine("table name " + tabName + " exist!");
            return false;
        }
        MyDebug.printFileLine("table name: " + tabName);
        
        Symbol[] syms = (Symbol[])tab.getAllSymbols();
        if(syms != null) {
            for(int i = 0; i < syms.length; i++) {
                symsArray.add(syms[i]);
            }
        }
        addChildren(tabName, tab);
        return true;
    }
    
    /**
     * load all symbols in database to memory
     */
    public boolean loadAll() {
        VerilogDataBase db = new VerilogDataBase();
        db.init();
        
        String[] tabs = db.getAllTables(null);
        if(tabs == null || tabs.length == 0) {
            return false;
        }
        
        for(int i = 0; i < tabs.length; i++) {
            Symbol[] syms = db.retrive(tabs[i]);
            if(syms == null || syms.length == 0) {
                continue;
            }

            ArrayList<Symbol> symsArray = new ArrayList<Symbol>();
            for(int j = 0; j < syms.length; j++) {
                symsArray.add(syms[j]);
            }
            symbolMap.put(tabs[i], symsArray);
        }
        db.exit();
        return true;
    }
    
    /**
     * write back to database from memory
     */
    public boolean writeAll() {
        VerilogDataBase db = new VerilogDataBase();
        db.init();
        db.beginTransaction();
        
        Set<String> keys = symbolMap.keySet();
        Iterator<String> keyIter = keys.iterator();
        while (keyIter.hasNext()) {
           String tabName = keyIter.next();
           ArrayList<Symbol> syms = symbolMap.get(tabName);
           if(syms == null) {
               continue;
           }
           db.newTable(tabName, true);
           for(int i = 0; i < syms.size(); i++) {
               db.insert(tabName, syms.get(i));
           }
        }
        db.endTransaction();
        db.exit();
        return true;
    }
    
    /**
     * delete all symbol in database
     */
    public boolean deleteAll() {
        VerilogDataBase db = new VerilogDataBase();
        db.init();
        db.beginTransaction();
        db.clearAllTables();
        db.endTransaction();
        db.exit();
        symbolMap.clear();
        return true;
    }
    
    /**
     * write specified table and it's child table to database from memory
     */
    private boolean writeTable(VerilogDataBase db, String tabName) {
        ArrayList<Symbol> symsArray = symbolMap.get(tabName);
        if(symsArray == null)
            return false;
        db.newTable(tabName, true);
        for(int i = 0; i < symsArray.size(); i++) {
            db.insert(tabName, symsArray.get(i));
            String tabName1 = tabName + "#" + symsArray.get(i).name;
            writeTable(db, tabName1);
        }
        return true;
    }
    
    /**
     * write specified table to database from memory
     */
    private boolean writeTable(String tabName) {
        VerilogDataBase db = new VerilogDataBase();
        db.init();
        db.beginTransaction();
        
        writeTable(db, tabName);

        db.endTransaction();
        db.exit();
        return true;
    }
    
    /**
     * add one library
     * @param dir: library directory
     * @param libName: library name
     * @return
     */
    public boolean addDir(String dir) {
        FileList list = new FileList(dir, IParser.EXT_VERILOG);

        MyDebug.printFileLine("dir:" + dir + "======file num:" + list.getFileNum() + "========");

        // scan twice
        // the first scan files which not include others
        // the second scan other files
        for(int k = 0; k < 2; k++) {
            for(int i = 0; i < list.getFileNum(); i++) {
                String path = list.getFile(i);
                String tabName = getFileName(path);
                
                if(symbolMap.get(tabName) != null)
                    continue;    // table has been added
    
                try {
                    //MyDebug.printFileLine("index:" + i + ", file:" + path);
                    VerilogParser parser = new VerilogParser(true);
                    ASTNode sourceText = (ASTNode)parser.parse(path);
                    addTable(tabName, sourceText.getSymbolTable());
                    sourceText = null;
                    parser = null;
                    System.gc();
                    writeTable(tabName);
                }catch (SymbolNotFoundException e) {
                    continue;
                }catch (Exception e) {
                    StackTraceElement[] stackEle = e.getStackTrace();
                    MyDebug.printFileLine("stackEle.length:" + stackEle.length);
                    //if(stackEle.length > 7) {
                        MyDebug.printStackTrace(e);
                    //}
                }
            }
        }
        return true;
    }
    
    /**
     * get name of table which contain specified symbol in specified library
     * @return name of table, null if not found
     */
    public String getTableName(String libName, String symName) {
        Set<String> keys = symbolMap.keySet();
        Iterator<String> keyIter = keys.iterator();
        while (keyIter.hasNext()) {
           String tabName = keyIter.next();
           if(tabName.indexOf(libName) < 0) {
               continue;
           }
           ArrayList<Symbol> syms = symbolMap.get(tabName);
           if(syms == null) {
               continue;
           }
           
           for(int i = 0; i < syms.size(); i++) {
               if(syms.get(i).equals(symName)) {
                   return tabName;
               }
           }
        }
        
        return null;
    }
    
    protected HashMap<String, ArrayList<Symbol>> getSymbolMap() {
        return symbolMap;
    }
    
    /**
     * get symbol in specified table name
     */
    public Symbol[] getSymbol(String tabName, String symName) {
        ArrayList<Symbol> syms = symbolMap.get(tabName);
        if(syms == null) {
            return null;
        }
        
        if(symName == null || symName.isEmpty()) {
            if(syms.size() > 0)
                return syms.toArray(new Symbol[syms.size()]);
            else
                return null;
        }
        
        ArrayList<Symbol> ret = new ArrayList<Symbol>();
        for(int i = 0; i < syms.size(); i++) {
            if(syms.get(i).name.equals(symName)) {
                ret.add(syms.get(i));
            }
        }
        
        if(ret.size() > 0)
            return ret.toArray(new Symbol[ret.size()]);
        else
            return null;
    }
    
    public boolean isTableExist(String tabName) {
        return (symbolMap.get(tabName) != null);
    }
}
