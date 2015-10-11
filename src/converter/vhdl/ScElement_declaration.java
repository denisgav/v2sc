package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> element_declaration ::=
 *   <dd> identifier_list : element_subtype_definition ;
 */
class ScElement_declaration extends ScCommonDeclaration {
    ScElement_subtype_definition type = null;
    public ScElement_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTELEMENT_DECLARATION);
    }

    public String scString() {
        String ret = super.scString();
        ret += ";";
        return ret;
    }
}
