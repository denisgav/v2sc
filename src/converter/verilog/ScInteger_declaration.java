package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  integer_declaration  <br>
 *     ::= <b>integer</b>  list_of_register_variables  ; 
 */
class ScInteger_declaration extends ScVerilog {
    ScList_of_register_variables vars = null;
    public ScInteger_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTEGER_DECLARATION);
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
