package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> incomplete_type_declaration ::=
 *   <dd> <b>type</b> identifier ;
 */
class ScIncomplete_type_declaration extends ScVhdl {
    public ScIncomplete_type_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTINCOMPLETE_TYPE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}
