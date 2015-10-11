package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  scalar_expression  <br>
 *     A scalar expression is a one bit net <b>or</b> a bit-select of an expanded vector net. 
 */
class ScScalar_expression extends ScVerilog {
    public ScScalar_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSCALAR_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
