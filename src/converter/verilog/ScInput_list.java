package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  input_list  <br>
 *     ::=  level_input_list  <br>
 *     ||=  edge_input_list  
 */
class ScInput_list extends ScVerilog {
    public ScInput_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINPUT_LIST);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
