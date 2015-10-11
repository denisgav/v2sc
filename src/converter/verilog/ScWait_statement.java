package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   <b>wait</b> (  expression  )  statement_or_null
 */
class ScWait_statement extends ScVerilog {
    ScExpression exp = null;
    ScStatement_or_null statement = null;
    public ScWait_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTWAIT_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTSTATEMENT_OR_NULL:
                statement = new ScStatement_or_null(c);
                break;
            case ASTEXPRESSION:
                exp = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += "wait(" + exp.toString() + ")" + System.lineSeparator();
        ret += statement.toString();
        return ret;
    }
}
