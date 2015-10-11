package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  udp_instance  <br>
 *     ::= [ name_of_udp_instance ] (  terminal  {, terminal } ) 
 */
class ScUdp_instance extends ScVerilog {
    ScName_of_udp_instance name = null;
    ArrayList<ScTerminal> terminals = new ArrayList<ScTerminal>();
    public ScUdp_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUDP_INSTANCE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScTerminal term = null;
            switch(c.getId())
            {
            case ASTNAME_OF_UDP_INSTANCE:
                name = new ScName_of_udp_instance(c);
                break;
            case ASTTERMINAL:
                term = new ScTerminal(c);
                terminals.add(term);
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
