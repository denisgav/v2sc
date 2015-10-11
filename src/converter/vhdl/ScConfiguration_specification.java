package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> configuration_specification ::=
 *   <dd> <b>for</b> component_specification binding_indication ;
 */
class ScConfiguration_specification extends ScVhdl {
    public ScConfiguration_specification(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCONFIGURATION_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}
