package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> quantity_declaration ::=
 *   <dd> free_quantity_declaration
 *   <br> | branch_quantity_declaration
 *   <br> | source_quantity_declaration
 */
class ScQuantity_declaration extends ScVhdl {
    public ScQuantity_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTQUANTITY_DECLARATION);
    }

    public String scString() {
        warning("quantity_declaration not support");
        return "";
    }
}
