package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class regexp1
{ 
    static final int TEST_COMMON = 0;
    static final int TEST_HTML = 1;
    static final int TEST_ASTCONSTANTS = 2;
    static final int TEST_ASTNODES = 3;
    static final int TEST_ASTNODES1 = 4;
    
    static final int TEST_REGEXP = TEST_ASTNODES1;   // which will be tested?

    public static void main(String[] args)
    {
        switch(TEST_REGEXP)
        {
        case TEST_HTML:
            test_html(args);
            break;
        case TEST_ASTCONSTANTS:
            test_astconstants(args);
            break;
        case TEST_ASTNODES:
            test_astnodes(args);
            break;
        case TEST_ASTNODES1:
            test_astnodes1(args);
            break;
        case TEST_COMMON:
            test_html(args);
        default:
            break;
        }
    }

    static void test_common(String[] args)
    {
      //String str = "based_literal " + System.lineSeparator();
        String str = "<dd> decimal_literal | based_literal " + System.lineSeparator();
        String number = "343dQQQQQAAA_fdsf4334";
        //if(str.matches("[<>|_ 0-9a-z\r\n]+"))
        if(number.matches("[0-9_A-Za-z]+"))
            System.out.println("match!");
        else
            System.out.println("not match!");
    }

    static void test_html(String[] args)
    {
        /**
         * <p></p></dd><dt> <a name="pred004">actual_designator</a> ::=
              </dt><dd> <a href="#pred112">expression</a> 
              <br> | <i>signal_</i><a href="#pred173">name</a> 
              <br> | <i>variable_</i><a href="#pred173">name</a> 
              <br> | <i>file_</i><a href="#pred173">name</a> 
              <br> | <i>terminal_</i><a href="#pred173">name</a> 
              <br> | <i>quantity_</i><a href="#pred173">name</a> 
              <br> | <b>open</b>
         */
        String validLine = "[a-zA-Z0-9</>_= \"]*::=";
        
        String str = "<p></p></dd><dt> <a name=\"pred004\">actual_designator</a> ::=";
        if(str.matches(validLine))
            System.out.println("match!");
        else
            System.out.println("not match!");
        String dir = System.getProperty("user.dir");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dir + "\\src\\vhdl_lrm.htm"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "\\src\\a.java"));
            String line = "";
            while(line != null) {
                if(line.matches(validLine)) {
                    boolean first = true;
                    String name = "astName";
                    String wstr = "";
                    while(line != null && !line.isEmpty()) {
                        wstr = "";
                        if(first) {
                            wstr += "/**" + System.lineSeparator() + " * ";
                            name = getAstName(line);
                            first = false;
                        }else {
                            wstr += " *   ";
                        }
                        String tmp = removeUnused(line);
                        if(!tmp.isEmpty()) {
                            wstr += tmp;
                            wstr += System.lineSeparator();
                            writer.write(wstr);
                        }
                        line = reader.readLine();
                        if(line != null)
                            line = line.trim();
                    }
                    if(line != null && line.isEmpty()) {
                        writer.write(" */" + System.lineSeparator());
                    }
                    wstr = "void " + name + "(Node p) throws ParseException {" + System.lineSeparator();
                    wstr += "    AST" + name + " node = new AST" + name + "(p);" + System.lineSeparator();
                    wstr += "    openNodeScope(node);" + System.lineSeparator();
                    wstr += "    closeNodeScope(node);" + System.lineSeparator();
                    wstr += "}" + System.lineSeparator() + System.lineSeparator();
                    writer.write(wstr);
                }
                line = reader.readLine();
                if(line != null)
                    line = line.trim();
            }
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static String getAstName(String str)
    {
        String ret = "";
        int eid = str.indexOf("::=");
        if(eid < 0)
            return "";
        
        int idx1, idx2;
        idx1 = str.lastIndexOf("</a>");
        if(idx1 >= 0) {
            idx2 = str.lastIndexOf("<a ");
            if(idx2 >= 0) {
                idx2 = str.indexOf(">", idx2);
            }
            if(idx2 >= 0) {
                ret += str.substring(idx2+1, idx1);
            }
        }
        return ret;
    }
    
    static String removeLink(String str)
    {
        String ret = str;
        int idx1 = ret.indexOf("<a ");
        while(idx1 >= 0) {
            String tmp = ret.substring(0, idx1);
            idx1 = ret.indexOf('>', idx1);
            int idx2 = -1;
            if(idx1 >= 0) {
                idx2 = ret.indexOf("</a>", idx1);
            }
            if(idx1 >= 0 && idx2 >= 0) {
                tmp += ret.substring(idx1+1, idx2) + ret.substring(idx2+4);
            }
            ret = tmp;
            idx1 = ret.indexOf("<a ");
        }

        return ret;        
    }
    
    static String removeUnused(String str)
    {
        String amp = "&amp;";
        String gt = "&gt;";
        String lt = "&lt;";
        
        String ret = removeLink(str);
        if(ret.isEmpty())
            return ret;
        
        ret = ret.replaceAll("<p>", "");
        ret = ret.replaceAll("</p>", "");
        ret = ret.replaceAll("</dt>", "");
        ret = ret.replaceAll("</dd>", "");
        ret = ret.replaceAll("<dt>", "<dl>");
        ret = ret.replace(amp, "&");
        ret = ret.replace(gt, ">");
        ret = ret.replace(lt, "<");
        return ret;        
    }
    
    static void test_astconstants(String[] args)
    {
        final String strFind = "node = new ASTNode(";
        String className = "d";
        ArrayList<String> astArray = new ArrayList<String>();
        astArray.add("ASTvoid");
        
        String dir = System.getProperty("user.dir");
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(dir + "\\src\\parser\\vhdl\\VhdlParser.java"));
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(dir + "\\src\\parser\\vhdl\\" + className + ".java"));
            String line = reader.readLine();
            while(line != null) {
                line = line.trim();
                int index = line.indexOf(strFind);
                if(index >= 0) {
                    index = index+strFind.length();
                    index = line.indexOf(",") + 1;
                    int index1 = line.indexOf(")", index);
                    line = line.substring(index, index1);
                    line = line.trim();
                    astArray.add(line);
                }
                line = reader.readLine();
            }
            writer.write("public interface " + className + System.lineSeparator() + "{" + System.lineSeparator());
            for(int i = 0; i < astArray.size(); i++)
            {
                String upper = astArray.get(i).toUpperCase();
                String pl = "    static final int " + upper + " = " + i + ";" + System.lineSeparator();
                writer.write(pl);
            }
            writer.write(System.lineSeparator());
            writer.write("    static final String[] ASTNodeName = " + System.lineSeparator() + "    {" + System.lineSeparator());
            for(int i = 0; i < astArray.size(); i++)
            {
                String lower = astArray.get(i).toLowerCase().substring(3);
                String pl = "        \"" + lower + "\"," + System.lineSeparator();
                writer.write(pl);
            }
            writer.write("    };" + System.lineSeparator());
            writer.write("}" + System.lineSeparator());
            writer.flush();
            writer.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static class ASTnode {
        String comment;
        String name;
        String content;
    }
    
    /** extract ast nodes from html */
    static ArrayList<ASTnode> getNodesFromHtml(String htmlPath) {
        ArrayList<ASTnode> nodes = new ArrayList<ASTnode>();
        String validLine = "[a-zA-Z0-9</>_= \"]*::=";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(htmlPath));
            String line = "";
            while(line != null) {
                if(line.matches(validLine)) {
                    boolean first = true;
                    String name = "name";
                    String wstr0 = "";
                    while(line != null && !line.isEmpty()) {
                        if(first) {
                            wstr0 += "/**" + System.lineSeparator() + " * ";
                            name = getAstName(line);
                            first = false;
                        }else {
                            wstr0 += " *   ";
                        }
                        String tmp = removeUnused(line);
                        if(!tmp.isEmpty()) {
                            wstr0 += tmp;
                            wstr0 += System.lineSeparator();
                        }
                        line = reader.readLine();
                        if(line != null)
                            line = line.trim();
                    }
                    
                    if(line != null && line.isEmpty()) {
                        wstr0 += " */"+ System.lineSeparator();
                    }
                    ASTnode node = new ASTnode();
                    node.comment = wstr0;
                    node.name = name;
                    nodes.add(node);
                }
                line = reader.readLine();
                if(line != null)
                    line = line.trim();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }
    
    /** from html */
    static void test_astnodes(String[] args)
    {
        String validLine = "[a-zA-Z0-9</>_= \"]*::=";
        
        String str = "<p></p></dd><dt> <a name=\"pred004\">actual_designator</a> ::=";
        if(str.matches(validLine))
            System.out.println("match!");
        else
            System.out.println("not match!");
        String dir = System.getProperty("user.dir");
        String nodesDir = dir + "\\src\\parser\\vhdl\\astnodes";
        File f = new File(nodesDir);
        f.mkdirs();
        
        try {
            ArrayList<ASTnode> nodes = getNodesFromHtml(dir + "\\src\\vhdl_lrm.htm");
            for(int i = 0; i < nodes.size(); i++) {
                ASTnode node = nodes.get(i);
                String name = "AST" + node.name;
                BufferedWriter writer = new BufferedWriter(new FileWriter(nodesDir + "\\" + name + ".java"));
                
                String wstr = "package src.parser.vhdl.astnodes;" + System.lineSeparator() + System.lineSeparator();
                
                wstr += "import parser.vhdl.ASTNode;" + System.lineSeparator() + System.lineSeparator();
    
                wstr += node.comment;
                wstr += "class " + name + " extends ASTNode {" + System.lineSeparator();
                wstr += "    public " + name + "(ASTNode p) {" + System.lineSeparator();
                wstr += "        super(p, AST" + name.toUpperCase() + ");" + System.lineSeparator();
                wstr += "        assert(node.getId() == AST" + name.toUpperCase() + ");" + System.lineSeparator();
                wstr += "    }" + System.lineSeparator() + System.lineSeparator();
                wstr += "    public String scString() {" + System.lineSeparator();
                wstr += "        String ret = \"\";" + System.lineSeparator();
                wstr += "        return ret;" + System.lineSeparator();
                wstr += "    }" + System.lineSeparator();
                wstr += "}" + System.lineSeparator();
    
                wstr += System.lineSeparator();
                writer.write(wstr);
                writer.flush();
                writer.close();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /** from parser */
    static void test_astnodes1(String[] args)
    {
        final String strFind = "(IASTNode p, Token endToken) throws ParserException {";
        final String commentStart = "/**";
        final String commentEnd = "*/";
        
        String dir = System.getProperty("user.dir");
        String javadir = dir + "\\src\\parser\\vhdl\\astnodes";
        File f = new File(javadir);
        f.mkdirs();
        ArrayList<ASTnode> nodes = new ArrayList<ASTnode>();
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(dir + "\\src\\parser\\vhdl\\VhdlParser.java"));
            String line = reader.readLine();
            String comment = "";
            String content = "";
            boolean commentHasEnd = false;
            boolean contentHasEnd = true;
            while(line != null) {
                line = line.substring(4);
                if(!line.startsWith("//")) {
                    int index = line.indexOf(strFind);
                    if(index >= 0) {
                        int index1 = line.indexOf("void");
                        assert(index1 >= 0);
                        String name = line.substring(index1, index);
                        name.trim();
                        line = line.substring(index, index1);
                        line = line.trim();
                        ASTnode node = new ASTnode();
                        node.name = name;
                        node.comment = comment;
                        nodes.add(node);
                        comment = "";
                        content = "";
                        commentHasEnd = true;
                        contentHasEnd = false;
                    }else if(line.startsWith(commentStart)) {
                        comment = line;
                        commentHasEnd = false;
                        contentHasEnd = true;
                    }else if(!commentHasEnd) {
                        if(line.endsWith(commentEnd)) {
                            comment += System.lineSeparator();
                            commentHasEnd = true;
                        }
                        comment += line + System.lineSeparator();
                    }else if(!contentHasEnd) {
                        if(line.equals("}")) {
                            content += System.lineSeparator();
                            contentHasEnd = true;
                            nodes.get(nodes.size() - 1).content = content;
                        }else {
                            content += line + System.lineSeparator();
                        }
                    }
                }
                line = reader.readLine();
            }
                
            for(int i = 0; i < nodes.size(); i++) {
                ASTnode node = nodes.get(i);
                String name = "AST" + node.name;
                BufferedWriter writer = new BufferedWriter(new FileWriter(javadir + "\\" + name + ".java"));
                
                String wstr = "package src.parser.vhdl.astnodes;" + System.lineSeparator() + System.lineSeparator();
                
                wstr += "import parser.vhdl.ASTNode;" + System.lineSeparator() + System.lineSeparator();
    
                wstr += node.comment;
                wstr += "class " + name + " extends ASTNode {" + System.lineSeparator();
                wstr += "    public " + name + "(ASTNode p) {" + System.lineSeparator();
                wstr += "        super(p, AST" + name.toUpperCase() + ");" + System.lineSeparator();
                wstr += "        assert(node.getId() == AST" + name.toUpperCase() + ");" + System.lineSeparator();
                wstr += "    }" + System.lineSeparator() + System.lineSeparator();
                wstr += "    public String scString() {" + System.lineSeparator();
                wstr += "        String ret = \"\";" + System.lineSeparator();
                wstr += "        return ret;" + System.lineSeparator();
                wstr += "    }" + System.lineSeparator();
                wstr += "}" + System.lineSeparator();
    
                wstr += System.lineSeparator();
                writer.write(wstr);
                writer.flush();
                writer.close();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    static String trimRight(String str)
    {
        String ret = str;
        int index = str.length() - 1;
        if(index <= 0)
            return str;
        
        while(index > 0 && str.charAt(index) == ' ') {
            index --;
        }
        ret = str.substring(0, index + 1);
        return ret;
    }
}
 