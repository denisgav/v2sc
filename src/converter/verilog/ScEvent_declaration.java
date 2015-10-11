package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  event_declaration  <br>
 *     ::= <b>event</b>  name_of_event  {, name_of_event } ; 
 */
class ScEvent_declaration extends ScVerilog {
    ArrayList<ScName_of_event> events = new ArrayList<ScName_of_event>();
    public ScEvent_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEVENT_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScName_of_event evt  = null;
            switch(c.getId())
            {
            case ASTNAME_OF_EVENT:
                evt = new ScName_of_event(c);
                events.add(evt);
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
