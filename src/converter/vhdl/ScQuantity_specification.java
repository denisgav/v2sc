package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> quantity_specification ::=
 *   <dd> quantity_list : type_mark
 */
class ScQuantity_specification extends ScVhdl {
    public ScQuantity_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTQUANTITY_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}
