package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> group_constituent ::=
 *   <dd> name | character_literal
 */
class ScGroup_constituent extends ScVhdl {
    ScName name = null;
    String literal = "";
    public ScGroup_constituent(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGROUP_CONSTITUENT);
    }

    public String scString() {
        warning("group_constituent not support");
        return "";
    }
}
