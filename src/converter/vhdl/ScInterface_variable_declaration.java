package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> interface_variable_declaration ::=
 *   <dd> [ <b>variable</b> ] identifier_list : [ mode ] subtype_indication 
 *                  [ := <i>static_</i>expression ]
 */
class ScInterface_variable_declaration extends ScCommonDeclaration {
    public ScInterface_variable_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTINTERFACE_VARIABLE_DECLARATION);
    }
}
