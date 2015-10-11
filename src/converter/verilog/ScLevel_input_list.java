package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  level_input_list  <br>
 *     ::= { level_symbol }+ 
 */
class ScLevel_input_list extends ScVerilog {
    public ScLevel_input_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLEVEL_INPUT_LIST);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
