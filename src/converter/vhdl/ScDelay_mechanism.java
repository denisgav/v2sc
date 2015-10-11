package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> delay_mechanism ::=
 *   <dd> <b>transport</b>
 *   <br> | [ <b>reject</b> <i>time_</i>expression ] <b>inertial</b>
 */
class ScDelay_mechanism extends ScVhdl {
    public ScDelay_mechanism(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDELAY_MECHANISM);
    }

    public String scString() {
        return "";
    }
}
