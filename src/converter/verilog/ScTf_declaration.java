package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  tf_declaration  <br>
 *     ::=  parameter_declaration  <br>
 *     ||=  input_declaration  <br>
 *     ||=  output_declaration  <br>
 *     ||=  inout_declaration  <br>
 *     ||=  reg_declaration  <br>
 *     ||=  time_declaration  <br>
 *     ||=  integer_declaration  <br>
 *     ||=  real_declaration  
 */
class ScTf_declaration extends ScVerilog {
    public ScTf_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTF_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
