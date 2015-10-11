package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  conditional_port_expression  <br>
 *     ::=  port_reference  <br>
 *     ||=  unary_operator  port_reference  <br>
 *     ||=  port_reference  binary_operator  port_reference  
 */
class ScConditional_port_expression extends ScVerilog {
    ScPort_reference port_ref1 = null;
    ScPort_reference port_ref2 = null;
    ScVerilog op = null;
    public ScConditional_port_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONDITIONAL_PORT_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPORT_REFERENCE:
                if(port_ref1 == null) {
                    port_ref1 = new ScPort_reference(c);
                }else {
                    port_ref2 = new ScPort_reference(c);
                }
                break;
            case ASTUNARY_OPERATOR:
                op = new ScUnary_operator(c);
                break;
            case ASTBINARY_OPERATOR:
                op = new ScBinary_operator(c);
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
