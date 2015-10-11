package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_designator ::=
 *   <dd> entity_tag [ signature ]
 */
class ScEntity_designator extends ScVhdl {
    public ScEntity_designator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_DESIGNATOR);
    }

    public String scString() {
        return "";
    }
}
