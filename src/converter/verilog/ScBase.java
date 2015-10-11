package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  base  is one of the following tokens: <br>
 *     'b 'B 'o 'O 'd 'D 'h 'H 
 */
class ScBase extends ScVerilog {
    public ScBase(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBASE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
