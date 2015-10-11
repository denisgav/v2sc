package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  par_block  <br>
 *     ::= <b>fork</b> { statement } <b>join</b> <br>
 *     ||= <b>fork</b> :  name_of_block  { block_declaration } { statement } <b>join</b> 
 */
class ScPar_block extends ScVerilog {
    public ScPar_block(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPAR_BLOCK);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
