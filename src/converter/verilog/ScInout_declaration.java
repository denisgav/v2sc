package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  inout_declaration  <br>
 *     ::= <b>inout</b> [ range ]  list_of_variables  ; 
 */
class ScInout_declaration extends ScVerilog {
    ScList_of_variables vars = null;
    ScRange range = null;
    public ScInout_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINOUT_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTLIST_OF_VARIABLES:
                vars = new ScList_of_variables(c);
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
