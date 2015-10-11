package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  edge_descriptor  <br>
 *     ::= 01 <br>
 *     ||= 10 <br>
 *     ||= 0x <br>
 *     ||= x1 <br>
 *     ||= 1x <br>
 *     ||= x0 
 */
class ScEdge_descriptor extends ScVerilog {
    public ScEdge_descriptor(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEDGE_DESCRIPTOR);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
