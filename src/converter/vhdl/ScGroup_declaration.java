package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> group_declaration ::=
 *   <dd> <b>group</b> identifier : <i>group_template_</i>name ( group_constituent_list ) ;
 */
class ScGroup_declaration extends ScVhdl {
    public ScGroup_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTGROUP_DECLARATION);
    }

    public String scString() {
        warning("group_declaration not support");
        return "";
    }
}
