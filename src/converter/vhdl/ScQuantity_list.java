package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> quantity_list ::=
 *   <dd> <i>quantity_</i>name { , <i>quantity_</i>name }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class ScQuantity_list extends ScVhdl {
    public ScQuantity_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTQUANTITY_LIST);
    }

    public String scString() {
        return "";
    }
}
