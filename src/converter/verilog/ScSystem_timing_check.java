package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  system_timing_check  <br>
 *     ::= $setup(  timing_check_event ,  timing_check_event , <br>
 *          timing_check_limit  <br>
 *         [, notify_register ] ) ; <br>
 *     ||= $hold(  timing_check_event ,  timing_check_event , <br>
 *          timing_check_limit  <br>
 *         [, notify_register ] ) ; <br>
 *     ||= $period(  controlled_timing_check_event ,  timing_check_limit  <br>
 *         [, notify_register ] ) ; <br>
 *     ||= $width(  controlled_timing_check_event ,  timing_check_limit  <br>
 *         [, constant_expression ,  notify_register ] ) ; <br>
 *     ||= $skew(  timing_check_event ,  timing_check_event , <br>
 *          timing_check_limit  <br>
 *         [, notify_register ] ) ; <br>
 *     ||= $recovery(  controlled_timing_check_event , <br>
 *          timing_check_event , <br>
 *          timing_check_limit  [, notify_register ] ) ; <br>
 *     ||= $setuphold(  timing_check_event ,  timing_check_event , <br>
 *          timing_check_limit ,  timing_check_limit  [, notify_register ] ) ; 
 */
class ScSystem_timing_check extends ScVerilog {
    public ScSystem_timing_check(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSYSTEM_TIMING_CHECK);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
