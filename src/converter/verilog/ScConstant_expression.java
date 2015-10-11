package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  constant_expression  <br>
 *     ::= expression  
 */
class ScConstant_expression extends ScVerilog {
    ScExpression exp = null;
    public ScConstant_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTANT_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                exp = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return exp.scString();
    }
}
