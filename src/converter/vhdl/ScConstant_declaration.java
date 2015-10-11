package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> constant_declaration ::=
 *   <dd> <b>constant</b> identifier_list : subtype_indication [ := expression ] ;
 */
class ScConstant_declaration extends ScCommonDeclaration {

    public ScConstant_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCONSTANT_DECLARATION);
    }

    public String scString() {
        String ret = intent() + "const ";
        ret += super.scString();
        ret += ";";
        return ret;
    }
}
