package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  list_of_path_inputs  <br>
 *     ::=  specify_input_terminal_descriptor  {, specify_input_terminal_descriptor } 
 */
class ScList_of_path_inputs extends ScVerilog {
    public ScList_of_path_inputs(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_PATH_INPUTS);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
