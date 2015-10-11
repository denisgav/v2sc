package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_specification ::=
 *   <dd> entity_name_list : entity_class
 */
class ScEntity_specification extends ScVhdl {
    public ScEntity_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}
