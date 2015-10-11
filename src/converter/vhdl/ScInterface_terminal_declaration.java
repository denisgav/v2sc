package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> interface_terminal_declaration ::=
 *   <dd> <b>terminal</b> identifier_list : subnature_indication
 */
class ScInterface_terminal_declaration extends ScCommonDeclaration {
    public ScInterface_terminal_declaration(ASTNode node) {
        super(node, false);
        assert(node.getId() == ASTINTERFACE_TERMINAL_DECLARATION);
    }
}
