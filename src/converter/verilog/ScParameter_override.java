package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  parameter_override  <br>
 *     ::= <b>defparam</b>  list_of_param_assignments  ; 
 */
class ScParameter_override extends ScVerilog {
    ScList_of_param_assignments param = null;
    public ScParameter_override(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAMETER_OVERRIDE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTLIST_OF_PARAM_ASSIGNMENTS:
                param = new ScList_of_param_assignments(c);
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
