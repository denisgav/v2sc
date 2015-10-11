package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  edge_sensitive_path_declaration  <br>
 *     ::= [if (<expression>)] ([edge_identifier] <br>
 *          specify_input_terminal_descriptor  => <br>
 *         ( specify_output_terminal_descriptor  [ polarity_operator ] <br>
 *         :  data_source_expression )) =  path_delay_value ; <br>
 *     ||= [if (<expression>)] ([edge_identifier] <br>
 *          specify_input_terminal_descriptor  *> <br>
 *         ( list_of_path_outputs  [ polarity_operator ] <br>
 *         :  data_source_expression )) = path_delay_value ; 
 */
class ScEdge_sensitive_path_declaration extends ScVerilog {
    public ScEdge_sensitive_path_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEDGE_SENSITIVE_PATH_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
