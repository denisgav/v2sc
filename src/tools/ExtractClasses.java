package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ExtractClasses {
    static String dir = System.getProperty("user.dir") + "\\src\\converter\\vhdl\\";
    public static void main(String[] args)
    {
        String pkg = "";
        ArrayList<String> importClass = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dir+"ScVhdl.java"));
            reader.mark(2048);
            while(true)
            {
                try {
                    String line = reader.readLine();
                    if(line == null)
                        return;
                    line.trim();
                    if(line.indexOf("class") >= 0) {
                        break;
                    }else if(line.indexOf("package") >= 0) {
                        pkg = line;
                    }else if(line.indexOf("import") >= 0) {
                        importClass.add(line);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            reader.reset();
            extract(reader, pkg, importClass);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    static class classStruct
    {
        String pkg;             // package
        ArrayList<String> imp;  // imports
        ArrayList<String> impName; // import class Name
        String className;
        String content;
    }
    
    static int checkBracketClosed(String line) {
        int index = 0, count = 0;
        while(index < line.length()) {
            if(line.charAt(index) == '{')
                count ++;
            else if(line.charAt(index) == '}')
                count --;
            index ++;
        }
        
        return count;
    }
    
    static String readUntilRBracket(BufferedReader reader)
    {
        String ret = "";
        try {
            while(true)
            {
                String line = reader.readLine();
                if(line == null)
                    break;
                ret += line + System.lineSeparator();
                int c = checkBracketClosed(line);
                if(c > 0) {
                    ret += readUntilRBracket(reader);
                }else if(c < 0) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    static void GetImportName(ArrayList<String> impClass, ArrayList<String> impName)
    {
        for(int i = 0; i < impClass.size(); i++) {
            int index1 = impClass.get(i).lastIndexOf(".");
            int index2 = impClass.get(i).lastIndexOf(";");
            impName.add(impClass.get(i).substring(index1+1, index2));
        }
    }
    
    static void saveOne(classStruct c) {
        try {
            if(c.className.equals("ScVhdl"))
                return;
            
            ArrayList<String> myImp = new ArrayList<String>();
            
            StringTokenizer tkn = new StringTokenizer(c.content, "\r\n;.*+-/( \'\"=?!~|%&:,<>{");
            while(tkn.hasMoreTokens())
            {
                String n = tkn.nextToken();
                for(int i = 0; i < c.impName.size(); i++) {
                    if(n.equals(c.impName.get(i))) {
                        int j = 0;
                        for(j = 0; j < myImp.size(); j++) {
                            if(myImp.get(j).equals(c.imp.get(i)))
                                break;
                        }
                        if(j >= myImp.size())
                            myImp.add(c.imp.get(i));
                    }
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir+c.className+".java"));
            writer.write(c.pkg + System.lineSeparator() + System.lineSeparator());
            for(int i = 0; i < myImp.size(); i++)
                writer.write(myImp.get(i) + System.lineSeparator());
            writer.write(System.lineSeparator());
            writer.write(c.content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    static void extract(BufferedReader reader, String pkg, ArrayList<String> importClass)
    {
        String ret = "";
        String line;
        try {
            do {
                line = reader.readLine();
                if(line == null)
                    return;
                line.trim();
            }while(line.isEmpty() || line.indexOf("package") >= 0 
                    || line.indexOf("import") >= 0);
            
            classStruct c = null;
            ArrayList<String> impName = new ArrayList<String>();
            GetImportName(importClass, impName);
            ret = line + System.lineSeparator();
            while(true)
            {
                int index = line.indexOf("class");
                if(index >= 0) {
                    int index1 = line.indexOf(' ', index);
                    int index2 = line.indexOf(' ', index1+1);
                    c = new classStruct();
                    c.pkg = pkg;
                    c.imp = importClass;
                    c.impName = impName;
                    if(index2 > 0)
                        c.className = line.substring(index1+1, index2).trim();
                    else
                        c.className = line.substring(index1+1).trim();
                }
                String tmp = line.trim();
                
                if(!tmp.startsWith("*") && line.indexOf('{') >= 0) {
                    assert(c != null);
                    ret += readUntilRBracket(reader);
                    c.content = ret;
                    saveOne(c);
                    ret = "";
                    c = null;
                }
                
                line = reader.readLine();
                if(line == null)
                    return;
                ret += line + System.lineSeparator();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
