package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  charge_strength  <br>
 *     ::= ( <b>small</b> ) <br>
 *     ||= ( <b>medium</b> ) <br>
 *     ||= ( <b>large</b> ) 
 */
class ScCharge_strength extends ScVerilog {
    String image = "";
    public ScCharge_strength(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCHARGE_STRENGTH);
        image = node.getChild(0).toString();
    }

    public String scString() {
        return image;
    }
}
