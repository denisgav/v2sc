package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  list_of_assignments  <br>
 *     ::=  assignment  {, assignment } 
 */
class ScList_of_assignments extends ScVerilog {
    ArrayList<ScAssignment> assigns = new ArrayList<ScAssignment>();
    public ScList_of_assignments(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_ASSIGNMENTS);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScAssignment ass = null;
            switch(c.getId())
            {
            case ASTASSIGNMENT:
                ass = new ScAssignment(c);
                assigns.add(ass);
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
