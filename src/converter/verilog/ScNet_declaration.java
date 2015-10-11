package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  net_declaration  <br>
 *     ::=  nettype  [ expandrange ] [ delay ]  list_of_variables  ; <br>
 *     ||= <b>trireg</b> [ charge_strength ] [ expandrange ] [ delay ] list_of_variables  ; <br>
 *     ||=  nettype  [ drive_strength ] [ expandrange ] [ delay ]  list_of_assignments  ; 
 */
class ScNet_declaration extends ScVerilog {
    ScNettype nettype = null;
    ScRange range = null;
    ScDelay delay = null;
    ScList_of_variables vars = null;
    
    ScList_of_assignments assigns = null;
    ScCharge_strength charge_str = null;
    ScExpandrange expandrange = null;
    ScDrive_strength drive_str = null;
    public ScNet_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNET_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNETTYPE:
                nettype = new ScNettype(c);
                break;
            case ASTRANGE:
                range = new ScRange(c);
                break;
            case ASTDELAY:
                delay = new ScDelay(c);
                break;
            case ASTLIST_OF_VARIABLES:
                vars = new ScList_of_variables(c);
                break;
            case ASTLIST_OF_ASSIGNMENTS:
                assigns = new ScList_of_assignments(c);
                break;
            case ASTCHARGE_STRENGTH:
                charge_str = new ScCharge_strength(c);
                break;
            case ASTDRIVE_STRENGTH:
                drive_str = new ScDrive_strength(c);
                break;
            case ASTEXPANDRANGE:
                expandrange = new ScExpandrange(c);
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
