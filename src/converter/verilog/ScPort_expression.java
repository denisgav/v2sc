package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  port_expression  <br>
 *     ::=  port_reference  <br>
 *     ||= <b>{</b>  port_reference  {, port_reference } <b>}</b> 
 */
class ScPort_expression extends ScVerilog {
    public ScPort_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
