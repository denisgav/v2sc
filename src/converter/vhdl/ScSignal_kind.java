package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> signal_kind ::=
 *   <dd> <b>register</b> | <b>bus</b>
 */
class ScSignal_kind extends ScVhdl {
    String token = "";
    public ScSignal_kind(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGNAL_KIND);
        token = node.firstTokenImage();
    }

    public String scString() {
        return token;
    }
}
