package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  output_symbol  is one of the following characters: <br>
 *     0 1 x X 
 */
class ScOutput_symbol extends ScVerilog {
    public ScOutput_symbol(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOUTPUT_SYMBOL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
