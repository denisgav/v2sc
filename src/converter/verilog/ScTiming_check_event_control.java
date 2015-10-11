package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  timing_check_event_control  <br>
 *     ::= <b>posedge</b> <br>
 *     ||= <b>negedge</b> <br>
 *     ||=  edge_control_specifier  
 */
class ScTiming_check_event_control extends ScVerilog {
    public ScTiming_check_event_control(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTIMING_CHECK_EVENT_CONTROL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
