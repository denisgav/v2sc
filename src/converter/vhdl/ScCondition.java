package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> condition ::=
 *   <dd> <i>boolean_</i>expression
 */
class ScCondition extends ScVhdl {
    ScExpression expression = null;
    public ScCondition(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTCONDITION);
        expression = new ScExpression(node);
        expression.setLogic(true);
    }

    public String scString() {
        return expression.scString();
    }
}
