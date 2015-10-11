package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> source_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : subtype_indication source_aspect ;
 */
class ScSource_quantity_declaration extends ScVhdl {
    public ScSource_quantity_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSOURCE_QUANTITY_DECLARATION);
    }

    public String scString() {
        return "";
    }
}
