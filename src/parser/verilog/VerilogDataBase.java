package parser.verilog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import common.MyDebug;

import parser.verilog.Symbol;

public class VerilogDataBase
{
    // error string
    private static String JDBC_NOT_CONNECT = "jdbc has not been connect, call init() firstly";
    private static String INVALID_TABLE_NAME = "invalid table name";
    
    // database connection & statement
    private Connection conn = null;
    private Statement stmt = null;
    
    private String normalize(String str) {
        return str;
    }
    
    private String restore(String str) {
        return str;
    }
    
    public boolean init() {
        try {
            System.setProperty("java.library.path", "lib/sqlitejdbc.dll"); 
            
            // Loading database driver
            String driverName = "org.sqlite.JDBC";
            Class.forName(driverName);
            conn = DriverManager.getConnection("jdbc:sqlite:verilogLib.db");
            
            stmt = conn.createStatement();
        }catch (ClassNotFoundException e) {  
            e.printStackTrace();
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean newTable(String tabName, boolean append) {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return false;
        }
        
        if(tabName == null || tabName.isEmpty()) {
            MyDebug.printFileLine(INVALID_TABLE_NAME);
            return false;
        }
        
        tabName = normalize(tabName);
        
        String[] columns = {
                "name text", 
                "kind integer",
                "type text",
                "range0 text",
                "range1 text",
                "range2 text",
                "arrayRange0 text",
                "arrayRange1 text",
                "arrayRange2 text",
                "mode text",
                "defValue text",
                "paramList text"
        };
        
        String createStr = "";
        try {
            if(!append) {
                stmt.executeUpdate("drop table if exists \"" + tabName + "\";");
            }
            
            createStr = "create table \"" + tabName + "\"(";
            for(int i = 0; i < columns.length; i++) {
                createStr += columns[i];
                if(i < columns.length - 1) {
                    createStr += ", ";
                }
            }
            createStr += ");";
            stmt.executeUpdate(createStr);
        } catch (SQLException e) {
            //System.out.println(e.getMessage() + ": " + createStr);
            //e.printStackTrace();
            return false;
        }
        //MyDebug.printFileLine("create table:" + tabName);
        return true;
    }
    
    public boolean insert(String tabName, Symbol sym) {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return false;
        }
        
        if(tabName == null || tabName.isEmpty()) {
            MyDebug.printFileLine(INVALID_TABLE_NAME);
            return false;
        }
        tabName = normalize(tabName);
        
        if(sym == null) {
            MyDebug.printFileLine("null parameter");
            return false;
        }
        
        // check whether this symbol is already in table 
        Symbol[] oldSyms = retrive(tabName, sym.name);
        if(oldSyms != null) {
            for(int i = 0; i < oldSyms.length; i++) {
                if(oldSyms[i].equals(sym))
                    return true;
            }
        }
        
        String insertStr = "insert into \"" + tabName + "\" values(" +
                    "'" + normalize(sym.name) + "', " +
                    sym.kind + ", '" +  normalize(sym.type) + "', ";
        
        if(sym.range != null) {
            insertStr += "'" + normalize(sym.range[0]) + "', " 
                      + "'" + normalize(sym.range[1]) + "', "
                      + "'" + normalize(sym.range[2]) + "', ";
        }else {
            insertStr += "'', '', '', ";
        }
        
        if(sym.arrayRange != null) {
            insertStr += "'" + normalize(sym.arrayRange[0]) + "', " 
                      + "'" + normalize(sym.arrayRange[1]) + "', "
                      + "'" + normalize(sym.arrayRange[2]) + "', ";
        }else {
            insertStr += "'', '', '', ";
        }
        
        insertStr += "'" + normalize(sym.value) + "', ";
        
        if(sym.paramTypeList != null) {
            insertStr += "'";
            for(int i = 0; i < sym.paramTypeList.size(); i++) {
                insertStr += normalize(sym.paramTypeList.get(i));
                if(i < sym.paramTypeList.size() - 1) {
                    insertStr += "#";
                }
            }
            insertStr += "'";
        }else {
            insertStr += "''";
        }
        insertStr += ");";
        
        try {
            stmt.executeUpdate(insertStr);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean insert(String tabName, Symbol[] syms) {
        if(syms == null) {
            MyDebug.printFileLine("null parameter");
            return false;
        }
        
        for(int i = 0; i < syms.length; i++) {
            if(!insert(tabName, syms[i]))
                return false;
        }
        return true;
    }
    
    public Symbol[] retrive(String tabName) {
        return retriveArray(tabName, null);
    }
    
    public Symbol[] retrive(String tabName, String symName) {
        return retriveArray(tabName, symName);
    }
    
    public int getCount(String tabName) {
        return getCount(tabName, null);
    }
    
    public int getCount(String tabName, String symName) {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return 0;
        }
        if(tabName == null || tabName.isEmpty()) {
            MyDebug.printFileLine(INVALID_TABLE_NAME);
            return 0;
        }
        tabName = normalize(tabName);
        symName = normalize(symName);
        
        int ret = 0;
        
        String selStr = "select * from " + tabName;
        if(symName != null && !symName.isEmpty())
            selStr += " " + "where name = '" + symName  + "';";
        try {
            ResultSet rs = stmt.executeQuery(selStr);
            while(rs.next()) {
                ret ++;
            }
        }catch (SQLException e) {
            return 0;
        }
        return ret;
    }
    
    /**
     * find symbol in specified table
     * @param tableName name of table
     * @param symName symbol name
     */
    private Symbol[] retriveArray(String tabName, String symName) {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return null;
        }
        if(tabName == null || tabName.isEmpty()) {
            MyDebug.printFileLine(INVALID_TABLE_NAME);
            return null;
        }
        tabName = normalize(tabName);
        symName = normalize(symName);
        
        ArrayList<Symbol> ret = new ArrayList<Symbol>();
        String selStr = "select * from \"" + tabName + "\"";
        if(symName != null && !symName.isEmpty())
            selStr += " " + "where name = '" + symName  + "';";
        try {
            ResultSet rs = stmt.executeQuery(selStr);
            while(rs.next()) {
                Symbol sym = new Symbol();
                sym.name = restore(rs.getString("name"));
                sym.kind = rs.getInt("kind");
                sym.type = restore(rs.getString("type"));
                
                String tmp = rs.getString("range0");
                if(tmp != null && !tmp.isEmpty()) {
                    sym.range = new String[3];
                    sym.range[0] = restore(tmp);
                    sym.range[1] = restore(rs.getString("range1"));
                    sym.range[2] = restore(rs.getString("range2"));
                }
               
                //sym.mode = restore(rs.getString("mode"));
                sym.value = restore(rs.getString("defValue"));
                
                tmp = rs.getString("paramList");
                if(tmp != null && !tmp.isEmpty()) {
                    StringTokenizer tokenizer = new StringTokenizer(tmp, "#");
                    sym.paramTypeList = new ArrayList<String>();
                    while(tokenizer.hasMoreTokens()) {
                        sym.paramTypeList.add(restore(tokenizer.nextToken()));
                    }
                }
                ret.add(sym);
            }
            rs.close();
        }catch (SQLException e) {
            return null;
        }
        if(ret.size() > 0)
            return ret.toArray(new Symbol[ret.size()]);
        else
            return null;
    }
    
    /**
     * get name of table which contain the symbol 
     * @param tableNames name array of tables, if null, then search all tables
     * @param symName symbol name
     */
    public String getTableName(String[] tableNames, String symName) {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return "";
        }
        
       if(symName == null || symName.isEmpty()) {
           MyDebug.printFileLine("jdbc:invalid parameter");
           return "";
       }
        
        String ret = "";
        DatabaseMetaData meta;
        try {
            if(tableNames == null) {
                meta = conn.getMetaData();
                ResultSet res = meta.getTables(null, null, null, new String[] {"table"});
                int count = 0;
                while (res.next()) {
                    count ++;
                }
                tableNames = new String[count];
                res.first();
                res.previous();
                count = 0;
                while (res.next()) {
                    tableNames[count++] = (res.getString("table_name"));
                }
            }
            
            if(tableNames.length == 0) {
                return "";
            }
            
            for(int i = 0; i < tableNames.length; i++) {
                if(retrive(tableNames[i], symName) != null) {
                    ret = tableNames[i];
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    /**
     * clear contents of all tables
     */
    public boolean clearAllTables() {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return false;
        }

        ArrayList<String> tables = new ArrayList<String>();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, null, new String[] {"table"});
            while (res.next()) {
                tables.add(res.getString("table_name"));
            }
            res.close();
            
            for(int i = 0; i < tables.size(); i++) {
                clearTable(tables.get(i));
            }
            tables.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    /**
     * clear specified table
     * @param tabName
     */
    public boolean clearTable(String tabName) {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return false;
        }
        
        if(tabName == null || tabName.isEmpty()) {
            MyDebug.printFileLine(INVALID_TABLE_NAME);
            return false;
        }
        tabName = normalize(tabName);
        
        try {
            stmt.executeUpdate("drop table \"" + tabName + "\";");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    /**
     * check whether table exist
     */
    public boolean isTableExist(String tabName) {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return false;
        }
        
        if(tabName == null || tabName.isEmpty()) {
            MyDebug.printFileLine(INVALID_TABLE_NAME);
            return false;
        }
        
        tabName = normalize(tabName);
        
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, tabName, new String[] {"table"});
            if (res.next()) {
                return true;
            }
            res.close();
        } catch (SQLException e) {
            return false;
        }
        
        return false;
    }
    
    /**
     * get all table which's name contain nameSlice
     * <br> if namsSlice is null or empty, get all table in database
     */
    public String[] getAllTables(String nameSlice) {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return null;
        }
        
        nameSlice = normalize(nameSlice);
        
        ArrayList<String> ret = new ArrayList<String>();

        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, null, new String[] {"table"});
            while (res.next()) {
                String n = res.getString("table_name");
                n = restore(n);
                if(nameSlice == null || nameSlice.isEmpty() || n.indexOf(nameSlice) >= 0) {
                    ret.add(n);
                }
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        if(ret.size() == 0)
            return null;
        return ret.toArray(new String[ret.size()]);
    }
    
    public boolean beginTransaction() {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return false;
        }
        
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean endTransaction() {
        if(conn == null || stmt == null) {
            MyDebug.printFileLine(JDBC_NOT_CONNECT);
            return false;
        }
        
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
   
    /**
     * disconnect jdbc
     */
    public void exit() {
        try {
            if(stmt != null) {
                stmt.close();
            }
            
            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
