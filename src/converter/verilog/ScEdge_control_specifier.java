package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  edge_control_specifier  <br>
 *     ::= <b>edge</b> <b>[</b>  edge_descriptor {, edge_descriptor } <b>]</b> 
 */
class ScEdge_control_specifier extends ScVerilog {
    public ScEdge_control_specifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEDGE_CONTROL_SPECIFIER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
