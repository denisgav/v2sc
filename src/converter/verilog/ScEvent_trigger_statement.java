package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   ->  name_of_event  ;
 */
class ScEvent_trigger_statement extends ScVerilog {
    ScName_of_event name = null;
    public ScEvent_trigger_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTEVENT_TRIGGER_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNAME_OF_EVENT:
                name = new ScName_of_event(c);
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
