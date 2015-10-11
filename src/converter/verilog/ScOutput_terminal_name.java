package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  output_terminal_name  <br>
 *     ::=  name_of_variable  
 */
class ScOutput_terminal_name extends ScVerilog {
    public ScOutput_terminal_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOUTPUT_TERMINAL_NAME);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
