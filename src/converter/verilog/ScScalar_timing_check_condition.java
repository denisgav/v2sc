package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  scalar_timing_check_condition  <br>
 *     ::=  scalar_expression  <br>
 *     ||= ~ scalar_expression  <br>
 *     ||=  scalar_expression  ==  scalar_constant  <br>
 *     ||=  scalar_expression  ===  scalar_constant  <br>
 *     ||=  scalar_expression  !=  scalar_constant  <br>
 *     ||=  scalar_expression  !==  scalar_constant  
 */
class ScScalar_timing_check_condition extends ScVerilog {
    public ScScalar_timing_check_condition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSCALAR_TIMING_CHECK_CONDITION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
