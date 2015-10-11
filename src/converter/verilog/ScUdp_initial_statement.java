package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  udp_initial_statement  <br>
 *     ::= <b>initial</b>  output_terminal_name  =  init_val  ; 
 */
class ScUdp_initial_statement extends ScVerilog {
    public ScUdp_initial_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUDP_INITIAL_STATEMENT);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
