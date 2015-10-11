package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  udp_declaration  <br>
 *     ::=  output_declaration  <br>
 *     ||=  reg_declaration  <br>
 *     ||=  input_declaration  
 */
class ScUdp_declaration extends ScVerilog {
    public ScUdp_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUDP_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
