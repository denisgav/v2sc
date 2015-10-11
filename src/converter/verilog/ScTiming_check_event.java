package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  timing_check_event  <br>
 *     ::= [ timing_check_event_control ]  specify_terminal_descriptor  <br>
 *         [&&&  timing_check_condition ] 
 */
class ScTiming_check_event extends ScVerilog {
    public ScTiming_check_event(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTIMING_CHECK_EVENT);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
