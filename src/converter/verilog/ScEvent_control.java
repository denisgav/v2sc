package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  event_control  <br>
 *     ::= @  identifier  <br>
 *     ||= @ (  event_expression  ) 
 */
class ScEvent_control extends ScVerilog {
    public ScEvent_control(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEVENT_CONTROL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
