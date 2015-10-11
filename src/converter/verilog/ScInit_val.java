package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  init_val  <br>
 *     ::= 1'b0 <br>
 *     ||= 1'b1 <br>
 *     ||= 1'bx <br>
 *     ||= 1'bX <br>
 *     ||= 1'B0 <br>
 *     ||= 1'B1 <br>
 *     ||= 1'Bx <br>
 *     ||= 1'BX <br>
 *     ||= 1 <br>
 *     ||= 0 
 */
class ScInit_val extends ScVerilog {
    public ScInit_val(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINIT_VAL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
