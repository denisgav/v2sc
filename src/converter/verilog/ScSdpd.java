package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  sdpd  <br>
 *     ::= <b>if</b> (  sdpd_conditional_expression  )  path_description  =  path_delay_value ; 
 */
class ScSdpd extends ScVerilog {
    ScSdpd_conditional_expression condition = null;
    ScPath_description desc = null;
    ScPath_delay_value delay = null;
    public ScSdpd(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSDPD);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTSDPD_CONDITIONAL_EXPRESSION:
                condition = new ScSdpd_conditional_expression(c);
                break;
            case ASTPATH_DESCRIPTION:
                desc = new ScPath_description(c);
                break;
            case ASTPATH_DELAY_VALUE:
                delay = new ScPath_delay_value(c);
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
