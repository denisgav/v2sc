package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  scalar_constant  <br>
 *     ::= 1'b0 <br>
 *     ||= 1'b1 <br>
 *     ||= 1'B0 <br>
 *     ||= 1'B1 <br>
 *     ||= 'b0 <br>
 *     ||= 'b1 <br>
 *     ||= 'B0 <br>
 *     ||= 'B1 <br>
 *     ||= 1 <br>
 *     ||= 0 
 */
class ScScalar_constant extends ScVerilog {
    public ScScalar_constant(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSCALAR_CONSTANT);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
