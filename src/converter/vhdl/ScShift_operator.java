package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> shift_operator ::=
 *   <dd> <b>sll</b> | <b>srl</b> | <b>sla</b> | <b>sra</b> | <b>rol</b> | <b>ror</b>
 */
class ScShift_operator extends ScVhdl {
    ScToken op = null;
    public ScShift_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSHIFT_OPERATOR);
        op = new ScToken(node);
    }

    public String scString() {
        return op.scString();
    }
}
