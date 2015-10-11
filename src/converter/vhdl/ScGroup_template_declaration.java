package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> group_template_declaration ::=
 *   <dd> <b>group</b> identifier <b>is</b> ( entity_class_entry_list ) ;
 */
class ScGroup_template_declaration extends ScVhdl {
    public ScGroup_template_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTGROUP_TEMPLATE_DECLARATION);
    }

    public String scString() {
        warning("group_template_declaration not support");
        return "";
    }
}
