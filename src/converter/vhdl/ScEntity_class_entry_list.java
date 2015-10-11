package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_class_entry_list ::=
 *   <dd> entity_class_entry { , entity_class_entry }
 */
class ScEntity_class_entry_list extends ScVhdl {
    public ScEntity_class_entry_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_CLASS_ENTRY_LIST);
    }

    public String scString() {
        return "";
    }
}
