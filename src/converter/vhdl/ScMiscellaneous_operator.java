package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> miscellaneous_operator ::=
 *   <dd> ** | <b>abs</b> | <b>not</b>
 */
class ScMiscellaneous_operator extends ScVhdl {
    ScToken token = null;
    public ScMiscellaneous_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTMISCELLANEOUS_OPERATOR);
        token = new ScToken(node);
    }

    public String scString() {
        return token.scString();
    }
}
