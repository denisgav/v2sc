package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  specify_block  <br>
 *     ::= <b>specify</b> { specify_item } <b>endspecify</b> 
 */
class ScSpecify_block extends ScVerilog {
    ArrayList<ScSpecify_item> items = new ArrayList<ScSpecify_item>();
    public ScSpecify_block(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECIFY_BLOCK);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScSpecify_item item = null;
            switch(c.getId())
            {
            case ASTSPECIFY_ITEM:
                item = new ScSpecify_item(c);
                items.add(item);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
