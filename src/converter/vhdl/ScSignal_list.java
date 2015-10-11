package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> signal_list ::=
 *   <dd> <i>signal_</i>name { , <i>signal_</i>name }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class ScSignal_list extends ScVhdl {
    public ScSignal_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGNAL_LIST);
    }

    public String scString() {
        return "";
    }
}
