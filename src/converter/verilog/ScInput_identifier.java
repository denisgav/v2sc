package converter.verilog;

import parser.verilog.ASTNode;

/**
 * <input_identifier> <br>
 *     ::= the  IDENTIFIER  of a <b>module</b> <b>input</b> <b>or</b> <b>inout</b> terminal 
 */
class ScInput_identifier extends ScVerilog {
    public ScInput_identifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINPUT_IDENTIFIER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
