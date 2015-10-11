package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_tag ::=
 *   <dd> simple_name
 *   <br> | character_literal
 *   <br> | operator_symbol
 */
class ScEntity_tag extends ScVhdl {
    public ScEntity_tag(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_TAG);
    }

    public String scString() {
        return "";
    }
}
