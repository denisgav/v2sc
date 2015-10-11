package converter.verilog;

import parser.verilog.ASTNode;

/**
 * <edge_identifier> <br>
 *     ::= <b>posedge</b> <br>
 *     ||= <b>negedge</b> 
 */
class ScEdge_identifier extends ScVerilog {
    public ScEdge_identifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEDGE_IDENTIFIER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
