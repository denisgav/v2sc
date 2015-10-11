package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> relational_operator ::=
 *   <dd> = | /= | < | <= | > | >=
 */
class ScRelational_operator extends ScVhdl {
    ScToken op = null;
    public ScRelational_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTRELATIONAL_OPERATOR);
        op = new ScToken(node);
    }

    public String scString() {
        return getReplaceOperator(op.scString());
    }
}
