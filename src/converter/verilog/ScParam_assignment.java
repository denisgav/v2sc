package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  param_assignment  <br>
 *     ::= identifier  = constant_expression 
 */
class ScParam_assignment extends ScVerilog {
    ScIdentifier identifier = null;
    ScConstant_expression expression = null;
    public ScParam_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAM_ASSIGNMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTidentifier:
                identifier = new ScIdentifier(c);
                break;
            case ASTCONSTANT_EXPRESSION:
                expression = new ScConstant_expression(c);
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
