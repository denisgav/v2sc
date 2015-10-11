package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  continuous_assign  <br>
 *     ::= <b>assign</b> [ drive_strength ] [ delay ]  list_of_assignments  ; <br>
 *     ||=  nettype  [ drive_strength ] [ expandrange ] [ delay ]  list_of_assignments  ; 
 */
class ScContinuous_assign extends ScVerilog {
    ScNettype nettype = null;
    ScDrive_strength drive_str = null;
    ScDelay delay = null;
    ScExpandrange expandrange = null;
    ScList_of_assignments assigns = null;
    
    public ScContinuous_assign(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONTINUOUS_ASSIGN);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNETTYPE:
                nettype = new ScNettype(c);
                break;
            case ASTDELAY:
                delay = new ScDelay(c);
                break;
            case ASTLIST_OF_ASSIGNMENTS:
                assigns = new ScList_of_assignments(c);
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
