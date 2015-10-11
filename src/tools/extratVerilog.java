package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;


public class extratVerilog {
    public static void main(String[] args)
    {
        //test_astconstants(args);
        //test_html(args);    // parser
        test_html_converter(args);    // converter
    }
    
    static class AstNode
    {
        String name;
        ArrayList<String> content = new ArrayList<String>();
    }
    
    static class SortByName implements Comparator<AstNode>
    {
        public int compare(AstNode n1, AstNode n2){
            return n1.name.compareToIgnoreCase(n2.name);
        }
    }
    
    static void test_astconstants(String[] args)
    {
        String dir = System.getProperty("user.dir");
        
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(dir + "\\verilog_syntax.txt"));
            
            ArrayList<AstNode> astArray = new ArrayList<AstNode>();
            String line = reader.readLine();
            while(line != null) {
                int index = line.indexOf("::=");
                if(index > 0) {
                    String name = line.substring(0, index - 1).trim();
                    name = name.replace('-', '_');
                    name = name.replace(' ', '_');
                    AstNode newNode = new AstNode();
                    newNode.name = name;
                    while(!line.isEmpty()) {
                        newNode.content.add(line);
                        line = reader.readLine();
                        if(line == null || line.indexOf("::=") > 0) {
                            break;
                        }
                    }
                    astArray.add(newNode);
                }else {
                    line = reader.readLine();
                }
            }
            
            Collections.sort(astArray, new SortByName());
            writeInterface(dir, "VerilogASTConstants", astArray);
            writeParserClass(dir, "VerilogParser", astArray);
            
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    static void writeInterface(String dir, String className, ArrayList<AstNode> astArray)
    {
        if(astArray == null)
            return;
        
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(
                    new FileWriter(dir + "\\" + className + ".java"));
            String str = "";
            str += "package parser.verilog;" + System.lineSeparator() + System.lineSeparator();
            str += "public interface " + className + System.lineSeparator() + "{" + System.lineSeparator();
            for(int i = 0; i < astArray.size(); i++) {
                str += "    static final int ";
                str += "AST";
                if(!astArray.get(i).name.equalsIgnoreCase("identifier"))
                    str += astArray.get(i).name.toUpperCase();
                else
                    str += astArray.get(i).name;
                str += " = " + i + ";" + System.lineSeparator();
            }
            
            str += System.lineSeparator() + "    static final String[] ASTNodeName =" + System.lineSeparator();
            str += "    {" + System.lineSeparator();
            for(int i = 0; i < astArray.size(); i++) {
                str += "        \"" + astArray.get(i).name  + "\"";
                str += "," + System.lineSeparator();
            }
            str += "    };" + System.lineSeparator();
            str += "}" + System.lineSeparator();
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static void writeParserClass(String dir, String className, ArrayList<AstNode> astArray)
    {
        if(astArray == null)
            return;
        
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(
                    new FileWriter(dir + "\\" + className + ".java"));
            String str = "";
            str += "package parser.verilog;" + System.lineSeparator() + System.lineSeparator();
            str += "public class " + className;
            str += " implements IParser, VerilogTokenConstants, VerilogASTConstants" + System.lineSeparator() + "{" + System.lineSeparator();
            for(int i = 0; i < astArray.size(); i++) {
                
                str += "    /**" + System.lineSeparator();
                for(int j = 0; j < astArray.get(i).content.size(); j++) {
                    String tmp = addBold(astArray.get(i).content.get(j));
                    str += "     * " + parseBNF(astArray, tmp);
                    if(j < astArray.get(i).content.size() - 1)
                        str += "<br>";
                    str += System.lineSeparator();
                }
                str += "     */" + System.lineSeparator();
                
                String name = astArray.get(i).name;
                str += "    void " + name + "(IASTNode p, Token endToken) throws ParserException {" + System.lineSeparator();
                if(!astArray.get(i).name.equalsIgnoreCase("identifier"))
                    str += "        ASTNode node = new ASTNode(p, AST" + name.toUpperCase() + ");" + System.lineSeparator();
                else
                    str += "        ASTNode node = new ASTNode(p, AST" + name + ");" + System.lineSeparator();
                str += "        openNodeScope(node);" + System.lineSeparator();
                str += "        closeNodeScope(node);" + System.lineSeparator();
                str += "    }" + System.lineSeparator() + System.lineSeparator();
            }
            str += "}" + System.lineSeparator();
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static void writeConverterClass(String dir, ArrayList<AstNode> astArray)
    {
        if(astArray == null)
            return;
        
        boolean idenCreated = false;
        BufferedWriter writer;
        try {
            for(int i = 0; i < astArray.size(); i++) {
                
                String className = astArray.get(i).name;
                className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
                className = "Sc" + className;
                
                String filePath = dir + "\\" + className + ".java";
                String str = "";
                
                boolean isIdentifier = astArray.get(i).name.equalsIgnoreCase("identifier");
                File f = new File(filePath);
                if(!isIdentifier || !idenCreated) {
                    writer = new BufferedWriter(new FileWriter(filePath));
                    if(isIdentifier)
                        idenCreated = true;
                }else {
                    // identifier differ from IDENTIFIER, 
                    // we put them in one file
                    writer = new BufferedWriter(new FileWriter(filePath, true));
                }
                
                f.setWritable(true);
                if(f.length() == 0) {
                    str += "package converter.verilog;" + System.lineSeparator();
                    str += System.lineSeparator();
                    str += "import parser.verilog.ASTNode;" + System.lineSeparator();
                }
                
                str += System.lineSeparator();
                str += "/**" + System.lineSeparator();
                for(int j = 0; j < astArray.get(i).content.size(); j++) {
                    String tmp = addBold(astArray.get(i).content.get(j));
                    str += " * " + parseBNF(astArray, tmp);
                    if(j < astArray.get(i).content.size() - 1)
                        str += "<br>";
                    str += System.lineSeparator();
                }
                str += " */" + System.lineSeparator();
                
                str += "class " + className;
                str += " extends ScVerilog {" + System.lineSeparator();
                
                str += "    public " + className + "(ASTNode node) {" + System.lineSeparator();
                str += "        super(node);" + System.lineSeparator();
                if(!isIdentifier)
                    str += "        assert(node.getId() == AST" + astArray.get(i).name.toUpperCase() + ");" + System.lineSeparator();
                else
                    str += "        assert(node.getId() == AST" + astArray.get(i).name + ");" + System.lineSeparator();
                str += "    }" + System.lineSeparator() + System.lineSeparator();
                
                str += "    public String scString() {" + System.lineSeparator();
                str += "        String ret = \"\";" + System.lineSeparator();
                str += "        return ret;" + System.lineSeparator();
                str += "    }" + System.lineSeparator();
                
                str += "}" + System.lineSeparator();
                writer.write(str);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static String addBold(String str) {
        String ret = "";
        int i = 0;
        if(str.isEmpty())
            return "";
        
        while(i < str.length() && str.charAt(i) == ' ') {
            ret += ' ';
            i ++;
        }
        
        StringTokenizer tkn = new StringTokenizer(str);
        while(tkn.hasMoreTokens()) {
            String temp = tkn.nextToken();
            for(i = 0; i < tokenImage.length; i++) {
                if(temp.equals(tokenImage[i])) {
                    ret += "<b>" + temp + "</b>";
                    break;
                }
            }
            
            if(i >= tokenImage.length)
                ret += temp;
            ret += " ";
        }

        return ret;
    }
    
    static final String[] tokenImage =
    {
        "always",
        "and",
        "assign",
        "begin",
        "buf",
        "bufif0",
        "bufif1",
        "case",
        "casex",
        "casez",
        "cmos",
        "deassign",
        "default",
        "defparam",
        "disable",
        "edge",
        "else",
        "end",
        "endcase",
        "endmodule",
        "endfunction",
        "endprimitive",
        "endspecify",
        "endtable",
        "endtask",
        "event",
        "for",
        "force",
        "forever",
        "fork",
        "function",
        "highz0",
        "highz1",
        "if",
        "initial",
        "inout",
        "input",
        "integer",
        "join",
        "large",
        "macromodule",
        "medium",
        "module",
        "nand",
        "negedge",
        "nmos",
        "nor",
        "not",
        "notif0",
        "notif1",
        "or",
        "output",
        "parameter",
        "pmos",
        "posedge",
        "primitive",
        "pull0",
        "pull1",
        "pullup",
        "pulldown",
        "rcmos",
        "reg",
        "release",
        "repeat",
        "rnmos",
        "rpmos",
        "rtran",
        "rtranif0",
        "rtranif1",
        "scalared",
        "small",
        "specify",
        "specparam",
        "strength",
        "strong0",
        "strong1",
        "supply0",
        "supply1",
        "table",
        "task",
        "time",
        "tran",
        "tranif0",
        "tranif1",
        "tri",
        "tri0",
        "tri1",
        "triand",
        "trior",
        "trireg",
        "vectored",
        "wait",
        "wand",
        "weak0",
        "weak1",
        "while",
        "wire",
        "wor",
        "xnor",
        "xor",
        "real",
        "{",
        "}",
        "[",
        "]",
    };
    
    
    static void test_html(String[] args)
    {
        /**
         * <A name="REF1"></A><B><A href="#REF1">&lt;source_text&gt;</A></B>
         *     ::= &lt;<A href="#REF2">description</A>&gt;*
         *
         * <A name="REF2"></A><B><A href="#REF2">&lt;description&gt;</A></B>
         *     ::= &lt;<A href="#REF3">module</A>&gt;
         *     ||= &lt;<A href="#REF12">UDP</A>&gt;
         */
        String validLine = "<A name=\"REF[a-zA-Z0-9</>_= \"]+<A href=\"#REF[a-zA-Z0-9&;.:</>_= \"]+";
        
        String str = "<A name=\"REF1\"></A><B><A href=\"#REF1\">&lt;source_text&gt;</A></B>";
        if(str.matches(validLine))
            System.out.println("match!");
        else
            System.out.println("not match!");
        
        String str1 = "<H2><A name=\"REF0\"></A>1. Source Text</H2>";
        if(str1.matches(validLine))
            System.out.println("match!");
        else
            System.out.println("not match!");
        String dir = System.getProperty("user.dir");
        ArrayList<AstNode> astArray = new ArrayList<AstNode>();
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(dir + "\\src\\verilog_syntax.htm"));
            String line = reader.readLine();
            while(line != null) {
                if(line.matches(validLine)) {
                    String name = getAstName(line);
                    if(getIndex(astArray, name) >= 0 || name.indexOf("comment") >= 0) {
                        line = reader.readLine();
                        continue;
                    }
                    AstNode newNode = new AstNode();
                    newNode.name = name;
                    newNode.content.add(removeUnused(line));
                    while(true) {
                        line = reader.readLine();
                        if(line == null || line.matches(validLine) || line.isEmpty()) {
                            break;
                        }
                        String tmp = removeUnused(line);
                        newNode.content.add(tmp);
                    }
                    astArray.add(newNode);
                }else {
                    line = reader.readLine();
                }
            }
            
            AstNode newNode = new AstNode();
            newNode.name = "verilog_token";
            astArray.add(newNode);
            
            Collections.sort(astArray, new SortByName());
            writeInterface(dir, "VerilogASTConstants", astArray);
            writeParserClass(dir, "VerilogParser", astArray);
                    
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static void test_html_converter(String[] args)
    {
        /**
         * <A name="REF1"></A><B><A href="#REF1">&lt;source_text&gt;</A></B>
         *     ::= &lt;<A href="#REF2">description</A>&gt;*
         *
         * <A name="REF2"></A><B><A href="#REF2">&lt;description&gt;</A></B>
         *     ::= &lt;<A href="#REF3">module</A>&gt;
         *     ||= &lt;<A href="#REF12">UDP</A>&gt;
         */
        String validLine = "<A name=\"REF[a-zA-Z0-9</>_= \"]+<A href=\"#REF[a-zA-Z0-9&;.:</>_= \"]+";
        
        String str = "<A name=\"REF1\"></A><B><A href=\"#REF1\">&lt;source_text&gt;</A></B>";
        if(str.matches(validLine))
            System.out.println("match!");
        else
            System.out.println("not match!");
        
        String str1 = "<H2><A name=\"REF0\"></A>1. Source Text</H2>";
        if(str1.matches(validLine))
            System.out.println("match!");
        else
            System.out.println("not match!");
        String dir = System.getProperty("user.dir");
        ArrayList<AstNode> astArray = new ArrayList<AstNode>();
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(dir + "\\src\\verilog_syntax.htm"));
            String line = reader.readLine();
            while(line != null) {
                if(line.matches(validLine)) {
                    String name = getAstName(line);
                    if(getIndex(astArray, name) >= 0 || name.indexOf("comment") >= 0) {
                        line = reader.readLine();
                        continue;
                    }
                    AstNode newNode = new AstNode();
                    newNode.name = name;
                    newNode.content.add(removeUnused(line));
                    while(true) {
                        line = reader.readLine();
                        if(line == null || line.matches(validLine) || line.isEmpty()) {
                            break;
                        }
                        String tmp = removeUnused(line);
                        newNode.content.add(tmp);
                    }
                    astArray.add(newNode);
                }else {
                    line = reader.readLine();
                }
            }
            
            AstNode newNode = new AstNode();
            newNode.name = "verilog_token";
            astArray.add(newNode);
            
            Collections.sort(astArray, new SortByName());
            writeConverterClass(dir + "\\src\\converter\\verilog", astArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    static int getIndex(ArrayList<AstNode> astArray, String name)
    {
        int ret = -1;
        for(int i = 0; i < astArray.size(); i++) {
            if(astArray.get(i).name.equals(name)) {
                ret = i;
                break;
            }
        }
        return ret;
    }
    
    /*
     * Definition of Items in Formal Syntax Specifications:
+-----------------------+------------------------------------------------------------+
|      Item            |               Meaning                                     |
+-----------------------+------------------------------------------------------------+
| White space           | may be used to separate lexical tokens                     |
+-----------------------+------------------------------------------------------------+
| Angle brackets        | surround each description item and are not literal sym-    |
|                       | bols. That is, they do not appear in the source descrip-   |
|                       | tion. Any text outside angle brackets is literal.          |
+-----------------------+------------------------------------------------------------+
| <name> in lower case  | is a syntax construct item                                 |
+-----------------------+------------------------------------------------------------+
| <NAME> in upper case  | is a lexical token item. Its definition is a terminal node |
|                       | in the description hierarchy -- that is, its definition    |
|                       | does not contain any syntax construct items                |
+-----------------------+------------------------------------------------------------+
| <name>?               | is an optional item                                        |
+-----------------------+------------------------------------------------------------+
| <name>*               | is zero, one, or more items                                |
+-----------------------+------------------------------------------------------------+
| <name>+               | is one or more items                                       |
+-----------------------+------------------------------------------------------------+
| <name><,<name>>*      | is a comma-separated list of items with at least one       |
|                       | item in the list                                           |
+-----------------------+------------------------------------------------------------+
| <name>::=             | gives a syntax definition to an item                       |
+-----------------------+------------------------------------------------------------+
| ||=                   | introduces an alternative syntax definition                |
+-----------------------+------------------------------------------------------------+
     */
    
    static int getRAngleBracket(String str, int from) {
        int index = from + 1;
        while(index < str.length()) {
            if(str.charAt(index) == '>') {
                break;
            }else if(str.charAt(index) == '<') {
                index = getRAngleBracket(str, index) + 1;
            }
            index ++;
        }
        return index;
    }

    static String parseBNF(ArrayList<AstNode> astArray, String str)
    {
        String ret = str;
        String oldStr = str;
        if(str.indexOf("identifier") < 0)
            str = str.toUpperCase();
        int index;
        String tmp = "";
        String tmp1 = "", tmp2;
        
        for(int i = 0; i < astArray.size(); i++) {
            if(!astArray.get(i).name.equalsIgnoreCase("identifier")
                    && !astArray.get(i).name.equalsIgnoreCase("null")) {
                tmp = "<" + astArray.get(i).name.toUpperCase() + ">";
                tmp1 = " " + astArray.get(i).name.toLowerCase() + " ";
            } else {
                tmp = "<" + astArray.get(i).name + ">";
                tmp1 = " " + astArray.get(i).name + " ";
            }
            
            String tmp_q = tmp + "?";
            String tmp_q1 = "[" + tmp1 + "]";
            
            String tmp_m = tmp + "*";
            String tmp_m1 = "{" + tmp1 + "}";
            
            String tmp_a = tmp + "+";
            String tmp_a1 = "{" + tmp1 + "}+";
            
            index = str.indexOf(tmp_a);
            if(index >= 0) {
                tmp2 = oldStr.substring(index, index+tmp_a.length());
                ret = ret.replace(tmp2, tmp_a1);
            }
            
            index = str.indexOf(tmp_m);
            if(index >= 0) {
                tmp2 = oldStr.substring(index, index+tmp_m.length());
                ret = ret.replace(tmp2, tmp_m1);
            }
            
            index = str.indexOf(tmp_q);
            if(index >= 0) {
                tmp2 = oldStr.substring(index, index+tmp_q.length());
                ret = ret.replace(tmp2, tmp_q1);
            }
            
            index = str.indexOf(tmp);
            if(index >= 0) {
                tmp2 = oldStr.substring(index, index+tmp.length());
                ret = ret.replace(tmp2, tmp1);
            }
        }
        
        index = ret.indexOf('<');
        while(index >= 0) {
            int index2 = getRAngleBracket(ret, index);
            if(index2 < 0)
                break;
            if(index2 < ret.length() - 1 && ret.charAt(index2+1) == '?') {
                ret = ret.substring(0, index) + "[" + ret.substring(index+1, index2) + "]"
                        + ret.substring(index2+2);
            }
            
            if(index2 < ret.length() - 1 && ret.charAt(index2+1) == '*') {
                ret = ret.substring(0, index) + "{" + ret.substring(index+1, index2) + "}"
                        + ret.substring(index2+2);
            }
            
            if(index2 < ret.length() - 1 && ret.charAt(index2+1) == '+') {
                ret = ret.substring(0, index) + "{" + ret.substring(index+1, index2) + "}+"
                        + ret.substring(index2+2);
            }
            index = ret.indexOf('<', index+1);
        }
        return ret;
    }
    
    static String getAstName(String str)
    {
        int index1 = str.indexOf("&lt;");
        int index2 = str.indexOf("&gt;");
        if(index1 < 0 || index2 < 0)
            return "";
        
        String ret = str.substring(index1+4, index2).trim();
        ret = ret.replace('-', '_');
        ret = ret.replace(' ', '_');
        if(!ret.equalsIgnoreCase("identifier")
                && !ret.equalsIgnoreCase("null")) {
            ret = ret.toLowerCase();
        }
        return ret;
    }
    
    static String removeLink(String str, String token)
    {
        String ret = str;
        int idx1 = ret.indexOf("<" + token);
        while(idx1 >= 0) {
            String tmp = ret.substring(0, idx1);
            idx1 = ret.indexOf('>', idx1);
            int idx2 = -1;
            if(idx1 >= 0) {
                idx2 = ret.indexOf("</" + token + ">", idx1);
            }
            if(idx1 >= 0 && idx2 >= 0) {
                tmp += ret.substring(idx1+1, idx2) + ret.substring(idx2+4);
            }
            ret = tmp;
            idx1 = ret.indexOf("<" + token);
        }

        return ret;        
    }
    
    static String removeUnused(String str)
    {
        String amp = "&amp;";
        String gt = "&gt;";
        String lt = "&lt;";
        
        String ret = removeLink(str, "A");
        ret = removeLink(ret, "B");
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
        ret = ret.replace("\t", "    ");
        return ret;        
    }
}
