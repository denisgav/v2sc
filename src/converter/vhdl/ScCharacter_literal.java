package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> character_literal ::=
 *   <dd> ' graphic_character '
 */
class ScCharacter_literal extends ScVhdl {
    String str = "";
    public ScCharacter_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTCHARACTER_LITERAL);
        str = node.firstTokenImage();
    }

    public String scString() {
        return str;
    }
}
