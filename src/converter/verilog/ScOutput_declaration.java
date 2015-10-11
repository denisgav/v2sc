package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  output_declaration  <br>
 *     ::= <b>output</b> [ range ]  list_of_variables  ; 
 */
class ScOutput_declaration extends ScVerilog {
    ScList_of_variables vars = null;
    ScRange range = null;
    public ScOutput_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOUTPUT_DECLARATION);
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
