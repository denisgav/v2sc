package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> nature_declaration ::=
 *   <dd> <b>nature</b> identifier <b>is</b> nature_definition ;
 */
class ScNature_declaration extends ScVhdl {
    public ScNature_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTNATURE_DECLARATION);
    }

    public String scString() {
        warning("nature_declaration not support");
        return "";
    }
}
