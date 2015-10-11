package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  port  <br>
 *     ::= [ port_expression ] <br>
 *     ||= .  name_of_port  ( [ port_expression ] ) 
 */
class ScPort extends ScVerilog {
    ScName_of_port portName = null;
    ScPort_expression expression = null;
    public ScPort(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNAME_OF_PORT:
                portName = new ScName_of_port(c);
                break;
            case ASTPORT_EXPRESSION:
                expression = new ScPort_expression(c);
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
