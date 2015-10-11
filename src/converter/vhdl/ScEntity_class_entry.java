package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_class_entry ::=
 *   <dd> entity_class [ <> ]
 */
class ScEntity_class_entry extends ScVhdl {
    public ScEntity_class_entry(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_CLASS_ENTRY);
    }

    public String scString() {
        return "";
    }
}
