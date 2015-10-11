package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  polarity_operator  <br>
 *     ::= + <br>
 *     ||= - 
 */
class ScPolarity_operator extends ScVerilog {
    public ScPolarity_operator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPOLARITY_OPERATOR);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
