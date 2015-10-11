package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   non_blocking_assignment  ;
 */
class ScNon_block_assignment_statement extends ScVerilog {
    ScNon_blocking_assignment assign = null;
    public ScNon_block_assignment_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTNON_BLOCK_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTDELAY_OR_EVENT_CONTROL:
                assign = new ScNon_blocking_assignment(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return assign.toString() + ";" + System.lineSeparator();
    }
}
