package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  path_description  <br>
 *     ::= (  specify_input_terminal_descriptor  =>  specify_output_terminal_descriptor  ) <br>
 *     ||= (  list_of_path_inputs  *>  list_of_path_outputs  ) 
 */
class ScPath_description extends ScVerilog {
    public ScPath_description(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPATH_DESCRIPTION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
