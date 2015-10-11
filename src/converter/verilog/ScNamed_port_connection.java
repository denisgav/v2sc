package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  named_port_connection  <br>
 *     ::= . IDENTIFIER  ( [ expression ] ) 
 */
class ScNamed_port_connection extends ScVerilog {
    ScIDENTIFIER0 portId = null;
    ScExpression expression = null;
    public ScNamed_port_connection(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAMED_PORT_CONNECTION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                portId = new ScIDENTIFIER0(c);
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
        String ret = "";
        return ret;
    }
}
