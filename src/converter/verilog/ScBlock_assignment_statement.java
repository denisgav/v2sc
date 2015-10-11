package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   blocking_assignment  ;
 */
class ScBlock_assignment_statement extends ScVerilog {
    ScBlocking_assignment assign = null;
    public ScBlock_assignment_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTBLOCK_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCKING_ASSIGNMENT:
                assign = new ScBlocking_assignment(c);
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
