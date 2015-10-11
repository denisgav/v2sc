package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  level_sensitive_path_declaration  <br>
 *     ::= <b>if</b> ( conditional_port_expression ) <br>
 *         ( specify_input_terminal_descriptor  [ polarity_operator ] => <br>
 *          specify_output_terminal_descriptor ) =  path_delay_value ; <br>
 *     ||= <b>if</b> ( conditional_port_expression ) <br>
 *         ( list_of_path_inputs  [ polarity_operator ] *> <br>
 *          list_of_path_outputs ) =  path_delay_value ; <br>
 *     (Note: The following two symbols are literal symbols, <b>not</b> syntax description conventions:) <br>
 *         *> => 
 */
class ScLevel_sensitive_path_declaration extends ScVerilog {
    public ScLevel_sensitive_path_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLEVEL_SENSITIVE_PATH_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
