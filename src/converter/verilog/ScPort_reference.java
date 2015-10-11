package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  port_reference  <br>
 *     ::=  name_of_variable  <br>
 *     ||=  name_of_variable  <b>[</b>  constant_expression  <b>]</b> <br>
 *     ||=  name_of_variable  <b>[</b>  constant_expression  : constant_expression  <b>]</b> 
 */
class ScPort_reference extends ScVerilog {
    public ScPort_reference(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT_REFERENCE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
