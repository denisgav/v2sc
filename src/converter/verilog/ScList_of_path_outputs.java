package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  list_of_path_outputs  <br>
 *     ::=  specify_output_terminal_descriptor  {, specify_output_terminal_descriptor } 
 */
class ScList_of_path_outputs extends ScVerilog {
    public ScList_of_path_outputs(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_PATH_OUTPUTS);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
