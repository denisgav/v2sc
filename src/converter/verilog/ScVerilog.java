package converter.verilog;

import java.util.ArrayList;

import common.MyDebug;
import converter.CommentManager;

import parser.CommentBlock;
import parser.IParser;
import parser.Token;
import parser.verilog.ASTNode;
import parser.verilog.SystemConstants;
import parser.verilog.VerilogASTConstants;
import parser.verilog.VerilogTokenConstants;


public class ScVerilog implements SystemConstants, VerilogTokenConstants, 
                        VerilogASTConstants, ScVerilogConstants
{
    protected static IParser parser = null;
    protected static CommentManager commentMgr = null;
    
    protected static int curLevel = 0;    // intent level

    
    /** whether create .cpp&.h or .h only */
    protected static final boolean individual = true;
    
    /** whether this module is individual:
     * <br>to package: = individual,
     * <br>to generic entity: = false,
     * <br>to non generic entity: = individual 
     */ 
    protected static boolean curIndividual = individual; 
    protected static boolean isCommonDeclaration = false;
    
    protected ASTNode curNode = null;
    protected int beginLine = 0;
    protected int endLine = 0;
    protected ArrayList<CommentBlock> myPrevComment = null;
    protected CommentBlock myPostComment = null;
    protected boolean needComment = false;
    
    /**
     * constructor
     */
    public ScVerilog(IParser parser) {
        ScVerilog.parser = parser;
        if(parser != null) {
            curNode = (ASTNode)parser.getRoot();
            commentMgr = new CommentManager(parser);
            init();
        }
    }
    
    public ScVerilog(ASTNode node) {
        curNode = node;
        init();
    }
    
    public ScVerilog(ASTNode node, boolean needComment) {
        curNode = node;
        this.needComment = needComment;
        init();
    }
    
    protected void init() {
        if(curNode != null) {
            Token token = curNode.getFirstToken();
            if(token != null) {
                beginLine = token.beginLine;
            }else {
                beginLine = ((ASTNode)curNode.getParent()).getFirstToken().beginLine;
            }
            
            token = curNode.getLastToken();
            if(token != null) {
                endLine = token.beginLine;
            }else {
                endLine = ((ASTNode)curNode.getParent()).getLastToken().beginLine;
            }
        }
        
        if(needComment && commentMgr != null) {
            CommentBlock tmp = commentMgr.getCurrentBlock();
            if(tmp != null) {
                if(tmp.endLine < beginLine) {
                    if(myPrevComment == null)
                        myPrevComment = new ArrayList<CommentBlock>();
                    do {
                        myPrevComment.add(tmp);
                        commentMgr.toNextBlock();
                        tmp = commentMgr.getCurrentBlock();
                    }while(tmp != null && tmp.endLine < beginLine);
                }
                
                if(tmp != null && tmp.startLine == tmp.endLine 
                        && beginLine == endLine 
                        && tmp.startLine == endLine) {
                    // the comment is at the end of this line
                    myPostComment = tmp;
                    commentMgr.toNextBlock();
                }
            }
        }
    }
    
    protected String addPrevComment() {
        if(myPrevComment == null) return "";
        String ret = "";
        for(int i = 0; i < myPrevComment.size(); i ++) {
            CommentBlock cb = myPrevComment.get(i);
            for(int j = 0; j < cb.commentLines.size(); j++) {
                ret += intent() + cb.commentLines.get(j) + System.lineSeparator();
            }
        }
        return ret;
    }
    
    protected String addPostComment() {
        if(myPostComment == null) return "";
        String ret = "    ";
        ret += myPostComment.commentLines.get(0);
        return ret;
    }
    
    
    @Override
    public String toString() {
        String ret = "";
        ret += addPrevComment();
        ret += scString();
        ret += addPostComment();
        return ret;
    }
    
    public String scString() {
        return "";
    }
    
    protected void warning(String msg) {
        MyDebug.printFileLine0("line--" + beginLine + ": warning: " + msg);
    }
    
    protected void error() {
        MyDebug.printFileLine0("line--" + beginLine + ": =========not support========");
    }
    
    protected void error(String msg) {
        MyDebug.printFileLine0("line--" + beginLine + ": " + msg);
    }
    
    protected static void startIntentBlock() { curLevel ++; }
    protected static void endIntentBlock() { curLevel --; }
    
    protected String startIntentBraceBlock() {
        String ret = intent() + "{" + System.lineSeparator();
        curLevel ++;
        return ret;
    }
    
    protected String endIntentBraceBlock() {
        curLevel --;
        return intent() + "}" + System.lineSeparator();
    }
    
    // intent
    protected static String intent(int lv)
    {
        String ret = "";
        for (int i = 0; i < lv; i++)
            for (int j = 0; j < tabSize; j++)
                ret += " ";
        return ret;
    }
    
    protected String intent()
    {
        return intent(curLevel);
    }
    
    String getReplaceType(String type)
    {
        String ret = type;
        int[] rtypes = replaceFastTypes;
        if (!fastSimulation)
            rtypes = replaceTypes;

        for (int i = 0; i < rtypes.length; i++) {
            if (type.equalsIgnoreCase(verilogTypes[i])) {
                ret = scType[rtypes[i]];
                break;
            }
        }
        return ret;
    }
    
    boolean isRangeValid(String[] range)
    {
        return ((range != null) && (range[0] != null)
                && (range[0].length() > 0));
    }
    
    /**
     * calculate result of add one
     */
    static String addOne(String input)
    {
        String str, ret;
        int index1 = input.lastIndexOf('-');
        int index2 = input.lastIndexOf('+');
        int index = -1, addition = 0;
        if (index1 > 0) {
            index = index1;
            addition = -1;
        } else if (index2 > 0) {
            index = index2;
            addition = 1;
        }

        int value = 0;
        ret = "";
        if (index > 0) {
            str = input.substring(index + 1);
            str = str.trim();
            try {
                if(str.startsWith("0x")) {
                    str = str.substring(2);
                    value = Integer.parseInt(str, 16) + addition;
                }else {
                    value = Integer.parseInt(str, 10) + addition;
                }
            }catch(NumberFormatException e) {
                    return (input + " + 1");
            }
            
            if(value == 0) {
                ret = input.substring(0, index).trim();
            }else {
                ret = input.substring(0, index+1) + " ";
                ret += String.format("%d", value);
            }
        }else {
            try {
                if(input.startsWith("0x")) {
                    value = Integer.parseInt(input, 16) + 1;
                }else {
                    value = Integer.parseInt(input) + 1;
                }
                ret = String.format("%d", value);
            }catch(NumberFormatException e) {
                return (input + " + 1");
            }
        }
        return ret;
    }
    
    String getReplaceOperator(String token)
    {
        String ret = token;
        for (int i = 0; i < verilogOperators.length; i++) {
            if (token.equalsIgnoreCase(verilogOperators[i])) {
                    ret = scOperators[replaceOp[i]];
                break;
            }
        }
        return ret;
    }
    
    static String getReplaceValue(String str)
    {
        str.trim();

        String ret = str;
        if ((str.equals("\'0\'")) || (str.equalsIgnoreCase("false")))
            ret = "0";
        else if ((str.equals("\'1\'")) || (str.equalsIgnoreCase("true")))
            ret = "1";
        return ret;
    }
    
    static String getSCTime(String vhdlTime) {
        String ret = scTimeScale[SC_NS];
        for(int i = 0; i < verilogTimeScale.length; i++)
        {
            if(vhdlTime.equalsIgnoreCase(verilogTimeScale[i])) {
                ret = scTimeScale[i];
                break;
            }
        }
        return ret;
    }
    
    protected String addLF(String str) {
        if(!str.isEmpty())
            return str + System.lineSeparator();
        else
            return "";
    }
    
    protected String addLFIntent(String str) {
        if(str.isEmpty())
            return "";

        String ret = "";
        str = str.trim();
        int index = str.indexOf("//");
        // until not comment line
        while(index == 0) {
            int index2 = str.indexOf(System.lineSeparator());
            if(index2 < 0)
                break;
            ret += intent() + str.substring(0, index2+2);
            str = str.substring(index2+2).trim();
            index = str.indexOf("//");
        }
        ret += intent() + str + System.lineSeparator();
        return ret;
    }    

    static private boolean isBracketEnclosed(String str) {
        if(str.isEmpty())
            return false;
        if(str.charAt(0) == '(')
        {
            int i = 1;
            boolean ret = true;
            while(i < str.length() - 1) {
                char c = str.charAt(i);
                if(c == '\"') {
                    i ++;
                    while(i < str.length() && str.charAt(i) != '\"') {
                        i++;
                    }
                }else if(c == ',' || c == ')') {
                    ret = false;
                    break;
                }else if(c == '(') {
                    i ++;
                    while(i < str.length() && str.charAt(i) != ')') {
                        i++;
                    }
                }
                i ++;
            }
            return ret;
        }
        return false;
    }
    
    /**
     * add "(" and ")" at two sides if necessary
     */
    static protected String encloseBracket(String str) {
        String ret = "";
        if(!isBracketEnclosed(str)) {
            ret = "(" + str + ")";
        }else {
            ret = str;
        }
        return ret;
    }
    
    /**
     * force to add bracket, and eliminate redundant "()"
     */
    static protected String encloseBracket(String str, String bracket) {
        String ret = "";

        ret += bracket.charAt(0);
        if(!isBracketEnclosed(str)) {
            ret += str; 
        }else {
            ret += str.substring(1, str.length() - 1);
        }
        ret += bracket.charAt(1);
        return ret;
    }    
}

