package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> guarded_signal_specification ::=
 *   <dd> <i>guarded_</i>signal_list : type_mark
 */
class ScGuarded_signal_specification extends ScVhdl {
    public ScGuarded_signal_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGUARDED_SIGNAL_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}
