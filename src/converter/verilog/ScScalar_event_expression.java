package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  scalar_event_expression  <br>
 *     Scalar <b>event</b> expression is an expression that resolves to a one bit value. <br>
 * </PRE> 
 */
class ScScalar_event_expression extends ScVerilog {
    public ScScalar_event_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSCALAR_EVENT_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
