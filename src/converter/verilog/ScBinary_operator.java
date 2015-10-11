package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  binary_operator  is one of the following tokens: <br>
 *     + - * / % == != === !== && || < <= > >= & | ^ ^~ >> << 
 */
class ScBinary_operator extends ScVerilog {
    public ScBinary_operator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBINARY_OPERATOR);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
