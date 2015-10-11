package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> direction ::=
 *   <dd> <b>to</b> | <b>downto</b>
 */
class ScDirection extends ScVhdl {
    String dir = "to";
    public ScDirection(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDIRECTION);
        dir = node.firstTokenImage();
    }

    public String scString() {
        return dir;
    }
}
