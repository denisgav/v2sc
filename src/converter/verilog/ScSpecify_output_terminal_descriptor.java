package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  specify_output_terminal_descriptor  <br>
 *     ::= <output_identifier> <br>
 *     ||= <output_identifier> <b>[</b> <constant_expression> <b>]</b> <br>
 *     ||= <output_identifier> <b>[</b> <constant_expression> : <constant_expression> <b>]</b> 
 */
class ScSpecify_output_terminal_descriptor extends ScVerilog {
    public ScSpecify_output_terminal_descriptor(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECIFY_OUTPUT_TERMINAL_DESCRIPTOR);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
