package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  list_of_param_assignments  <br>
 *     ::= param_assignment {, param_assignment } 
 */
class ScList_of_param_assignments extends ScVerilog {
    ArrayList<ScParam_assignment> params = new ArrayList<ScParam_assignment>();
    public ScList_of_param_assignments(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_PARAM_ASSIGNMENTS);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScParam_assignment param = null;
            switch(c.getId())
            {
            case ASTPARAM_ASSIGNMENT:
                param = new ScParam_assignment(c);
                params.add(param);
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
