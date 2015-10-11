package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> variable_assignment_statement ::=
 *   <dd> [ label : ] target := expression ;
 */
class ScVariable_assignment_statement extends ScVhdl {
    ScTarget target = null;
    ScExpression expression = null;
    public ScVariable_assignment_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTVARIABLE_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTARGET:
                target = new ScTarget(c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent();
        ret += target.scString();
        ret += " = ";
        ret += expression.scString();
        ret += ";";
        return ret;
    }
}
