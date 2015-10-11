package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  udp_instantiation  <br>
 *     ::=  name_of_udp  [ drive_strength ] [ delay ] <br>
 *      udp_instance  {, udp_instance } ; 
 */
class ScUdp_instantiation extends ScVerilog {
    ScName_of_udp udpName = null;
    ScDrive_strength drive_str = null;
    ScDelay delay = null;
    ArrayList<ScUdp_instance> udps = new ArrayList<ScUdp_instance>();
    public ScUdp_instantiation(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUDP_INSTANTIATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScUdp_instance udp = null;
            switch(c.getId())
            {
            case ASTNAME_OF_UDP:
                udpName = new ScName_of_udp(c);
                break;
            case ASTDRIVE_STRENGTH:
                drive_str = new ScDrive_strength(c);
                break;
            case ASTDELAY:
                delay = new ScDelay(c);
                break;
            case ASTUDP_INSTANCE:
                udp = new ScUdp_instance(c);
                udps.add(udp);
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
