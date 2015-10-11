package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  range_or_type  <br>
 *     ::=  range  <br>
 *     ||= <b>integer</b> <br>
 *     ||= <b>real</b> 
 */
class ScRange_or_type extends ScVerilog {
    public ScRange_or_type(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRANGE_OR_TYPE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
