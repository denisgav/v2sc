package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> nature_element_declaration ::=
 *   <dd> identifier_list : element_subnature_definition
 */
class ScNature_element_declaration extends ScVhdl {
    public ScNature_element_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNATURE_ELEMENT_DECLARATION);
    }

    public String scString() {
        return "";
    }
}
