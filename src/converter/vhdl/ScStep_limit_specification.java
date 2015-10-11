package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> step_limit_specification ::=
 *   <dd> <b>limit</b> quantity_specification <b>with</b> <i>real_</i>expression ;
 */
class ScStep_limit_specification extends ScVhdl {
    public ScStep_limit_specification(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSTEP_LIMIT_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}
