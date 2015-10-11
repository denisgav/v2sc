package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  parameter_value_assignment  <br>
 *     ::= # (  expression  {, expression } ) 
 */
class ScParameter_value_assignment extends ScVerilog {
    ArrayList<ScExpression> exps = new ArrayList<ScExpression>();
    public ScParameter_value_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAMETER_VALUE_ASSIGNMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScExpression exp = null;
            switch(c.getId())
            {
            case ASTEXPRESSION:
                exp = new ScExpression(c);
                exps.add(exp);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
