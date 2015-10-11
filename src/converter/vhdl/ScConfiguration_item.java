package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> configuration_item ::=
 *   <dd> block_configuration
 *   <br> | component_configuration
 */
class ScConfiguration_item extends ScVhdl {
    public ScConfiguration_item(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONFIGURATION_ITEM);
    }

    public String scString() {
        return "";
    }
}
