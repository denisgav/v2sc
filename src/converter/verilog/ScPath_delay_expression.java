package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  path_delay_expression  <br>
 *     ::=  mintypmax_expression  
 */
class ScPath_delay_expression extends ScVerilog {
    public ScPath_delay_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPATH_DELAY_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
