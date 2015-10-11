package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  specify_input_terminal_descriptor  <br>
 *     ::= <input_identifier> <br>
 *     ||= <input_identifier> <b>[</b> <constant_expression> <b>]</b> <br>
 *     ||= <input_identifier> <b>[</b> <constant_expression> : <constant_expression> <b>]</b> 
 */
class ScSpecify_input_terminal_descriptor extends ScVerilog {
    public ScSpecify_input_terminal_descriptor(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECIFY_INPUT_TERMINAL_DESCRIPTOR);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
