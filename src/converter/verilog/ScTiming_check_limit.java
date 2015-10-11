package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  timing_check_limit  <br>
 *     ::=  expression  
 */
class ScTiming_check_limit extends ScVerilog {
    public ScTiming_check_limit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTIMING_CHECK_LIMIT);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
