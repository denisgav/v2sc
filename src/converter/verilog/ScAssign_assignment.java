package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   <b>assign</b>  assignment  ;
 */
class ScAssign_assignment extends ScVerilog {
    ScAssignment assign = null;
    public ScAssign_assignment(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTASSIGN_ASSIGNMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTASSIGN_ASSIGNMENT:
                assign = new ScAssignment(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String scString() {
        String ret = assign.scString() + ";" + System.lineSeparator();
        return ret;
    }
}
