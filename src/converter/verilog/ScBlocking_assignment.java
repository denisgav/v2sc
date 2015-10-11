package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  blocking_assignment  <br>
 *     ::=  lvalue  =  expression  <br>
 *     ||=  lvalue  =  delay_or_event_control   expression  
 */
class ScBlocking_assignment extends ScVerilog {
    ScLvalue lvalue = null;
    ScDelay_or_event_control delay = null;
    ScExpression exp = null;
    public ScBlocking_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCKING_ASSIGNMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTLVALUE:
                lvalue = new ScLvalue(c);
                break;
            case ASTDELAY_OR_EVENT_CONTROL:
                delay = new ScDelay_or_event_control(c);
                break;
            case ASTEXPRESSION:
                exp = new ScExpression(c);
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
