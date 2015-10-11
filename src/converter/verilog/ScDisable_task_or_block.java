package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   <b>disable</b>  name_of_task  ; <br>
 *   <b>disable</b>  name_of_block  ;
 */
class ScDisable_task_or_block extends ScVerilog {
    ScVerilog name = null;
    public ScDisable_task_or_block(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTDISABLE_TASK_OR_BLOCK);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNAME_OF_TASK:
                name = new ScName_of_task(c);
                break;
            case ASTNAME_OF_BLOCK:
                name = new ScName_of_block(c);
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
