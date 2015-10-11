package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  sdpd_conditional_expression  <br>
 *     ::= expression  binary_operator  expression  <br>
 *     ||= unary_operator  expression  
 */
class ScSdpd_conditional_expression extends ScVerilog {
    public ScSdpd_conditional_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSDPD_CONDITIONAL_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
