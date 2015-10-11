package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  delay_or_event_control  <br>
 *     ::=  delay_control  <br>
 *     ||=  event_control  <br>
 *     ||= <b>repeat</b> (  expression  )  event_control  
 */
class ScDelay_or_event_control extends ScVerilog {
    ScVerilog control = null;
    ScExpression expression = null;
    public ScDelay_or_event_control(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDELAY_OR_EVENT_CONTROL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTDELAY_CONTROL:
                control = new ScDelay_control(c);
                break;
            case ASTEVENT_CONTROL:
                control = new ScEvent_control(c);
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
        if(expression == null) {
            ret = control.scString();
        }else {
            // repeat
            ret += "";
        }
        return ret;
    }
}
