package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  range  <br>
 *     ::= <b>[</b>  constant_expression  :  constant_expression  <b>]</b> 
 */
class ScRange extends ScVerilog {
    ScConstant_expression exp1 = null, exp2 = null;
    public ScRange(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRANGE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTCONSTANT_EXPRESSION:
                if(exp1 == null) {
                    exp1 = new ScConstant_expression(c);
                }else {
                    exp2 = new ScConstant_expression(c);
                }
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
