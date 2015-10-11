package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> string_literal ::=
 *   <dd> " { graphic_character } "
 */
class ScString_literal extends ScVhdl {
    String str = "";
    public ScString_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSTRING_LITERAL);
        str = node.firstTokenImage();
    }

    public String scString() {
        return str;
    }
}
