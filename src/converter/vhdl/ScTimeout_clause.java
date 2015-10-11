package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> timeout_clause ::=
 *   <dd> <b>for</b> <i>time_or_real_</i>expression
 */
class ScTimeout_clause extends ScVhdl {
    ScVhdl expression = null;
    String time_unit_name = "ns";
    public ScTimeout_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTIMEOUT_CLAUSE);
        expression = new ScExpression((ASTNode)node.getChild(0));
        time_unit_name = node.getLastToken().image;
    }

    public String scString() {
        return expression.scString();
    }
    
    public String getTimeUnitName() {
        return time_unit_name;
    }
}
