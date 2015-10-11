package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  specparam_declaration  <br>
 *     ::= <b>specparam</b>  list_of_param_assignments  ; 
 */
class ScSpecparam_declaration extends ScVerilog {
    ScList_of_param_assignments param = null;
    public ScSpecparam_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECPARAM_DECLARATION);
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
