package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  edge  <br>
 *     ::= (  level_symbol   level_symbol  ) <br>
 *     ||=  edge_symbol  
 */
class ScEdge extends ScVerilog {
    public ScEdge(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEDGE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
