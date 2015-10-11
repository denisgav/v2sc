package parser;

import java.util.ArrayList;

public class CommentBlock
{
    public int startLine;
    public int endLine;
    public ArrayList<String> commentLines; // content of comment
    
    public CommentBlock(int sl)
    {
        startLine = sl;
        endLine = sl;
        commentLines = new ArrayList<String>();
    }
    
    public CommentBlock(int sl, int el)
    {
        startLine = sl;
        endLine = el;
        commentLines = new ArrayList<String>();
    }
}
