package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> free_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : subtype_indication [ := expression ] ;
 */
class ScFree_quantity_declaration extends ScCommonDeclaration {
    public ScFree_quantity_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTFREE_QUANTITY_DECLARATION);
    }

    public String scString() {
        String ret = intent() + super.scString();
        ret += ";";
        return ret;
    }
}
