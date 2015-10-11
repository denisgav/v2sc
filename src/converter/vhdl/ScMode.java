package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> mode ::=
 *   <dd> <b>in</b> | <b>out</b> | <b>inout</b> | <b>buffer</b> | <b>linkage</b>
 */
class ScMode extends ScVhdl {
    String token = "";
    public ScMode(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODE);
        token = node.firstTokenImage();
    }

    public String scString() {
        return token;
    }
}
