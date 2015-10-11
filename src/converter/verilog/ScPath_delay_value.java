package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  path_delay_value  <br>
 *     ::=  path_delay_expression  <br>
 *     ||= (  path_delay_expression ,  path_delay_expression  ) <br>
 *     ||= (  path_delay_expression ,  path_delay_expression , <br>
 *          path_delay_expression ) <br>
 *     ||= (  path_delay_expression ,  path_delay_expression , <br>
 *          path_delay_expression ,  path_delay_expression , <br>
 *          path_delay_expression ,  path_delay_expression  ) <br>
 *     ||= (  path_delay_expression ,  path_delay_expression , <br>
 *          path_delay_expression ,  path_delay_expression , <br>
 *          path_delay_expression ,  path_delay_expression , <br>
 *          path_delay_expression ,  path_delay_expression , <br>
 *          path_delay_expression ,  path_delay_expression , <br>
 *          path_delay_expression ,  path_delay_expression  ) 
 */
class ScPath_delay_value extends ScVerilog {
    public ScPath_delay_value(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPATH_DELAY_VALUE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
