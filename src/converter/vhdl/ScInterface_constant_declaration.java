package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> interface_constant_declaration ::=
 *   <dd> [ <b>constant</b> ] identifier_list : [ <b>in</b> ] 
 *          subtype_indication [ := <i>static_</i>expression ]
 */
class ScInterface_constant_declaration extends ScCommonDeclaration {
    public ScInterface_constant_declaration(ASTNode node) {
        super(node, false);
        assert(node.getId() == ASTINTERFACE_CONSTANT_DECLARATION);
    }

    public String scString() {
        String ret = intent() + "const ";
        ret += super.scString();
        return ret;
    }
}
