package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   delay_or_event_control   statement_or_null
 */
class ScDelay_or_event_statement extends ScVerilog {
    ScDelay_or_event_control delay_or_event = null;
    ScStatement_or_null statement = null;
    public ScDelay_or_event_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTDELAY_OR_EVENT_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTDELAY_OR_EVENT_CONTROL:
                delay_or_event = new ScDelay_or_event_control(c);
                break;
            case ASTSTATEMENT_OR_NULL:
                statement = new ScStatement_or_null(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String scString() {
        String ret = addLF(delay_or_event.scString());
        ret += addLF(statement.scString());
        return ret;
    }
}
