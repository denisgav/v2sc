package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  assignment  <br>
 *     ::=  lvalue  =  expression  
 */
class ScAssignment extends ScVerilog {
    ScLvalue lvalue = null;
    ScExpression expression = null;
    public ScAssignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSIGNMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTLVALUE:
                lvalue = new ScLvalue(c);
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
        String ret = lvalue.scString();
        ret += ".write(";
        ret += expression.scString();
        ret += ")";
        return ret;
    }
}
