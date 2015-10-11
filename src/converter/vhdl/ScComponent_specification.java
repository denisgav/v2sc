package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> component_specification ::=
 *   <dd> instantiation_list : <i>component_</i>name
 */
class ScComponent_specification extends ScVhdl {
    public ScComponent_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPONENT_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}
