package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   <b>force</b>  assignment  ;
 */
class ScForce_statement extends ScVerilog {
    ScAssignment assign = null;
    public ScForce_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTFORCE_ASSIGNMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTASSIGNMENT:
                assign = new ScAssignment(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String scString() {
        return addLF(assign.scString() + ";");
    }
}
