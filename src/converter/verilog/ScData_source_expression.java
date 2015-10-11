package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  data_source_expression  <br>
 *     Any expression, including constants <b>and</b> lists. Its width must be one bit <b>or</b> <br>
 *     equal to the destination's width. If the destination is a list, the data <br>
 *     source must be as wide as the sum of the bits of the members. 
 */
class ScData_source_expression extends ScVerilog {
    public ScData_source_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDATA_SOURCE_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
