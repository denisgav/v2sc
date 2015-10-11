package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  reg_declaration  <br>
 *     ::= <b>reg</b> [ range ]  list_of_register_variables  ; 
 */
class ScReg_declaration extends ScVerilog {
    ScList_of_register_variables vars = null;
    ScRange range = null;
    public ScReg_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTREG_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTLIST_OF_REGISTER_VARIABLES:
                vars = new ScList_of_register_variables(c);
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
