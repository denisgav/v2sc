package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  mintypmax_expression  <br>
 *     ::=  expression  <br>
 *     ||=  expression  :  expression  :  expression  
 */
class ScMintypmax_expression extends ScVerilog {
    public ScMintypmax_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMINTYPMAX_EXPRESSION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
