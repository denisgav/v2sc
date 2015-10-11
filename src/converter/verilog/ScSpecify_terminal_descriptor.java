package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  specify_terminal_descriptor  <br>
 *     ::=  specify_input_terminal_descriptor  <br>
 *     ||= specify_output_terminal_descriptor  
 */
class ScSpecify_terminal_descriptor extends ScVerilog {
    public ScSpecify_terminal_descriptor(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECIFY_TERMINAL_DESCRIPTOR);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
