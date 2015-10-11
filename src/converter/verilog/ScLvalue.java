package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  lvalue  <br>
 *     ::=  identifier  <br>
 *     ||=  identifier  <b>[</b> <expression> <b>]</b> <br>
 *     ||=  identifier  <b>[</b> constant_expression : constant_expression <b>]</b> <br>
 *     ||=  concatenation  
 */
class ScLvalue extends ScVerilog {
    ScIdentifier identifier = null;
    ScConstant_expression exp1 = null, exp2 = null;
    ScConcatenation concat = null;
    public ScLvalue(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLVALUE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTidentifier:
                identifier = new ScIdentifier(c);
                break;
            case ASTCONSTANT_EXPRESSION:
                if(exp1 == null) {
                    exp1 = new ScConstant_expression(c);
                }else {
                    exp2 = new ScConstant_expression(c);
                }
                break;
            case ASTCONCATENATION:
                concat = new ScConcatenation(c);
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
