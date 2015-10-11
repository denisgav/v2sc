package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  level_symbol  is one of the following characters: <br>
 *     0 1 x X ? b B 
 */
class ScLevel_symbol extends ScVerilog {
    public ScLevel_symbol(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLEVEL_SYMBOL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
