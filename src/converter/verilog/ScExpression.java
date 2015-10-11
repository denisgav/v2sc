package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  expression  <br>
 *     ::=  primary  <br>
 *     ||=  unary_operator   primary  <br>
 *     ||=  expression   binary_operator   expression  <br>
 *     ||=  expression  ?  expression  :  expression  <br>
 *     ||=  string  
 */
class ScExpression extends ScVerilog {
    ScPrimary primary = null;
    ScUnary_operator unary_op = null;
    ScBinary_operator binary_op = null;
    ScExpression exp1 = null, exp2 = null, exp3 = null;
    String str;
    public ScExpression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPRIMARY:
                primary = new ScPrimary(c);
                break;
            case ASTEXPRESSION:
                if(exp1 == null) {
                    exp1 = new ScExpression(c);
                }else if(exp2 == null) {
                    exp2 = new ScExpression(c);
                }else {
                    exp3 = new ScExpression(c);
                }
                break;
            case ASTUNARY_OPERATOR:
                unary_op = new ScUnary_operator(c);
                break;
            case ASTBINARY_OPERATOR:
                binary_op = new ScBinary_operator(c);
                break;
            case ASTVERILOG_TOKEN:
                str = c.firstTokenImage();
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
