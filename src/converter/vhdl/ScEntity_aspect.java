package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_aspect ::=
 *   <dd> <b>entity</b> <i>entity_</i>name [ ( <i>architecture_</i>identifier ) ]
 *   <br> | <b>configuration</b> <i>configuration_</i>name
 *   <br> | <b>open</b>
 */
class ScEntity_aspect extends ScVhdl {
    public ScEntity_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_ASPECT);
    }

    public String scString() {
        return "";
    }
}
