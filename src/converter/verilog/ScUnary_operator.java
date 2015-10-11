package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  unary_operator  is one of the following tokens: <br>
 *     + - ! ~ & ~& | ^| ^ ~^ 
 */
class ScUnary_operator extends ScVerilog {
    public ScUnary_operator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUNARY_OPERATOR);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
