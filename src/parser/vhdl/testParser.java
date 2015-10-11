package parser.vhdl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Stack;

import parser.IASTNode;
import parser.ParserException;

public class testParser implements VhdlASTConstants
{   
    static final boolean debugOut = true;
    protected PrintStream debugStreamOut = null;
    
    protected void initDebugStream(String path) throws IOException
    {
        if(!debugOut)
            return;
        
        int index1;
        path.replace('\\', '/');
        index1 = path.lastIndexOf('.');
        if(index1 < 0)
            return;

        File file = new File(path.substring(0, index1) + ".txt");
        if(file.exists())
            file.delete();
        file.createNewFile();
        debugStreamOut = new PrintStream(file);
    }    
   
    protected void printTreeNode(IASTNode node)
    {
        assert(debugStreamOut != null);
        
        int i;
        Stack<Character> stack = new Stack<Character>();
        
        IASTNode parent = node.getParent();
        IASTNode grandParent = null;
        if(parent != null)
        {
            stack.push(' ');
            stack.push(' ');
            for(i = 0; i < parent.getChildrenNum(); i++)
            {
                if(node.equals(parent.getChild(i)))
                    break;
            }
            if(i >= parent.getChildrenNum() - 1)
                stack.push(' ');
            else
                stack.push(' ');
        }
        
        while(parent != null)
        {
            grandParent = parent.getParent();
            if(grandParent == null)
                break;
            for(i = 0; i < grandParent.getChildrenNum(); i++)
            {
                if(parent.equals(grandParent.getChild(i)))
                    break;
            }
            stack.push(' ');
            stack.push(' ');
            if(i >= grandParent.getChildrenNum() - 1)
            {
                stack.push(' ');
            }
            else
            {
                stack.push(' ');
            }
            parent = grandParent;
        }
        
        while(stack.size() > 0)
        {
            debugStreamOut.print(stack.pop());
        }
    }
    
    int maxLevel = 0;
    public void parserTree(ASTNode node, int level)
    {
        
        if(node == null)
            return;
        if(debugOut)
        {
            printTreeNode(node);
            debugStreamOut.println(level + "=" + node.toString() + ": "
                        + "<<" + node.firstTokenImage() + ">>");
        }
        
        if(maxLevel < level) {
            maxLevel = level;
        }
        
        for(int i = 0; i < node.getChildrenNum(); i++)
        {
            parserTree((ASTNode)node.getChild(i), level + 1);
        }
    }
    
    public void printStatistics() {
        String str1 = "=======================Statistics=========================" + System.lineSeparator();
        String str2 = "    max level: " + maxLevel + System.lineSeparator();
        String str3 = "==========================================================" + System.lineSeparator();
        String str = str1 + str2 + str3;
        if(debugOut && debugStreamOut != null) {
            debugStreamOut.println(str);
        }else {
            System.out.println(str);
        }
    }
    
    /** test vhdlparser */
    public static void main(String[] argv)
    {
        try {
            String dir = System.getProperty("user.dir");
            VhdlParser parser = new VhdlParser(false);
            String name = "vhdl_test" + File.separator + "leaves";
            ASTNode designFile = (ASTNode)parser.parse(dir + File.separator + name + ".vhd");
            testParser vhdl = new testParser();
            vhdl.initDebugStream(name + ".txt");
            vhdl.parserTree(designFile, 0);
            vhdl.printStatistics();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

