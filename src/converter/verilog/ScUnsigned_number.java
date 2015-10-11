package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  unsigned_number  <br>
 *     ::= A number containing a set of any of the following characters: <br>
 *             0123456789_ 
 */
class ScUnsigned_number extends ScVerilog {
    public ScUnsigned_number(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUNSIGNED_NUMBER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
