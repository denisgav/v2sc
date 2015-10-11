package converter.verilog;

import parser.verilog.ASTNode;

/**
 * <output_identifier> <br>
 *     ::= the  IDENTIFIER  of a <b>module</b> <b>output</b> <b>or</b> <b>inout</b> terminal. 
 */
class ScOutput_identifier extends ScVerilog {
    public ScOutput_identifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOUTPUT_IDENTIFIER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
