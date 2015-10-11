package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> interface_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : [ <b>in</b> | <b>out</b> ] 
 *              subtype_indication [ := <i>static_</i>expression ]
 */
class ScInterface_quantity_declaration extends ScCommonDeclaration {
    public ScInterface_quantity_declaration(ASTNode node) {
        super(node, false);
        assert(node.getId() == ASTINTERFACE_QUANTITY_DECLARATION);
    }
}
