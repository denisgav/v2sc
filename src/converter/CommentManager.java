package converter;

import parser.CommentBlock;
import parser.IParser;

public class CommentManager
{
    protected CommentBlock[] comments = null;
    protected int curIndex = -1;
    
    public CommentManager(IParser parser)
    {
        if(parser != null) {
            comments = parser.getComment();
            if(comments != null) {
                curIndex = 0;
            }
        }
    }
    
    public CommentBlock getCurrentBlock() {
        if(comments == null || curIndex < 0 
            || curIndex >= comments.length) {
            return null;
        }
        return comments[curIndex];
    }
    
    public void toNextBlock() {
        if(comments == null) { return; }
        if(curIndex < comments.length - 1) {
            curIndex ++;
        }else {
            curIndex = -1;
        }
    }
}
