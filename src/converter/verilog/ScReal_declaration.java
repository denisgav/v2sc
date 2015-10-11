package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  real_declaration  <br>
 *     ::= <b>real</b>  list_of_variables  ; 
 */
class ScReal_declaration extends ScVerilog {
    ScList_of_variables vars = null;
    public ScReal_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTREAL_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTLIST_OF_VARIABLES:
                vars = new ScList_of_variables(c);
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
