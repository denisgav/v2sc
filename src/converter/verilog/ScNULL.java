package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  NULL  <br>
 *     ::= nothing - this form covers the <b>case</b> of an empty item in a list - <b>for</b> example: <br>
 *           (a, b, , d) 
 */
class ScNULL extends ScVerilog {
    public ScNULL(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNULL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
