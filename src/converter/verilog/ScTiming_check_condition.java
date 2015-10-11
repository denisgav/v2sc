package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  timing_check_condition  <br>
 *     ::=  scalar_timing_check_condition  <br>
 *     ||= (  scalar_timing_check_condition  ) 
 */
class ScTiming_check_condition extends ScVerilog {
    public ScTiming_check_condition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTIMING_CHECK_CONDITION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
