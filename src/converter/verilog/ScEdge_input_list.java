package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  edge_input_list  <br>
 *     ::= { level_symbol }  edge  { level_symbol } 
 */
class ScEdge_input_list extends ScVerilog {
    public ScEdge_input_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEDGE_INPUT_LIST);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
