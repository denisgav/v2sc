package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> sign ::=
 *   <dd> + | -
 */
class ScSign extends ScVhdl {
    public ScSign(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGN);
    }

    public String scString() {
        return "";
    }
}
