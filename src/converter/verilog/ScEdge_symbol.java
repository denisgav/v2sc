package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  edge_symbol  is one of the following characters: <br>
 *     r R f F p P n N * 
 */
class ScEdge_symbol extends ScVerilog {
    public ScEdge_symbol(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEDGE_SYMBOL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
