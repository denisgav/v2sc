package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  string  is text enclosed in "" <b>and</b> contained on one line. 
 */
class ScString extends ScVerilog {
    public ScString(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTRING);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
