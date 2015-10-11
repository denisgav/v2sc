package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> group_constituent_list ::=
 *   <dd> group_constituent { , group_constituent }
 */
class ScGroup_constituent_list extends ScVhdl {
    public ScGroup_constituent_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGROUP_CONSTITUENT_LIST);
    }

    public String scString() {
        warning("group_constituent_list not support");
        return "";
    }
}
