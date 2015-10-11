package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> interface_signal_declaration ::=
 *   <dd> [ <b>signal</b> ] identifier_list : [ mode ] subtype_indication 
 *          [ <b>bus</b> ] [ := <i>static_</i>expression ]
 */
class ScInterface_signal_declaration extends ScCommonDeclaration {
    public ScInterface_signal_declaration(ASTNode node) {
        super(node, false);
        assert(node.getId() == ASTINTERFACE_SIGNAL_DECLARATION);
    }
}
