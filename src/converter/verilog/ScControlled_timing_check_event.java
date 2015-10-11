package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  controlled_timing_check_event  <br>
 *     ::=  timing_check_event_control   specify_terminal_descriptor  <br>
 *         [&&&  timing_check_condition ] 
 */
class ScControlled_timing_check_event extends ScVerilog {
    public ScControlled_timing_check_event(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONTROLLED_TIMING_CHECK_EVENT);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
