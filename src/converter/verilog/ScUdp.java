package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  udp  <br>
 *     ::= <b>primitive</b>  name_of_udp  (  name_of_variable  <br>
 *         {, name_of_variable } ) ; <br>
 *         { udp_declaration }+ <br>
 *         [ udp_initial_statement ] <br>
 *          table_definition  <br>
 *         <b>endprimitive</b> 
 */
class ScUdp extends ScVerilog {
    public ScUdp(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUDP);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
