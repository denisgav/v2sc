package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  parameter_declaration  <br>
 *     ::= <b>parameter</b> [ range ] list_of_param_assignments  ; 
 */
class ScParameter_declaration extends ScVerilog {
    ScList_of_param_assignments params = null;
    ScRange range = null;
    public ScParameter_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAMETER_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTLIST_OF_PARAM_ASSIGNMENTS:
                params = new ScList_of_param_assignments(c);
                break;
            case ASTRANGE:
                range = new ScRange(c);
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
