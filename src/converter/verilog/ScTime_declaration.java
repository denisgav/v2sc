package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  time_declaration  <br>
 *     ::= <b>time</b>  list_of_register_variables  ; 
 */
class ScTime_declaration extends ScVerilog {
    ScList_of_register_variables vars = null;
    public ScTime_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTIME_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTLIST_OF_REGISTER_VARIABLES:
                vars = new ScList_of_register_variables(c);
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
