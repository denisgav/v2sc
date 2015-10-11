package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> condition_clause ::=
 *   <dd> <b>until</b> condition
 */
class ScCondition_clause extends ScVhdl {
    ScCondition condition = null;
    public ScCondition_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONDITION_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return condition.scString();
    }
}
