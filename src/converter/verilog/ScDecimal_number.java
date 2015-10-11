package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  decimal_number  <br>
 *     ::= A number containing a set of any of the following characters, optionally preceded by + <b>or</b> - <br>
 *          0123456789_ 
 */
class ScDecimal_number extends ScVerilog {
    public ScDecimal_number(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDECIMAL_NUMBER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
