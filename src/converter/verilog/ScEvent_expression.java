package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  event_expression  <br>
 *     ::=  expression  <br>
 *     ||= <b>posedge</b>  scalar_event_expression  <br>
 *     ||= <b>negedge</b>  scalar_event_expression  <br>
 *     ||=  event_expression  <b>or</b>  event_expression  
 */
class ScEvent_expression extends ScVerilog {
    public ScEvent_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEVENT_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
