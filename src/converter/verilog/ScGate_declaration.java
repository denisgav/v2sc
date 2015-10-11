package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  gate_declaration  <br>
 *     ::=  gatetype  [ drive_strength ] [ delay ]  gate_instance  {, gate_instance } ; 
 */
class ScGate_declaration extends ScVerilog {
    ScGatetype gatetype = null;
    ScDrive_strength drive_str = null;
    ScDelay delay = null;
    ArrayList<ScGate_instance> gates = new ArrayList<ScGate_instance>();
    public ScGate_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGATE_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScGate_instance gate = null;
            switch(c.getId())
            {
            case ASTGATETYPE:
                gatetype = new ScGatetype(c);
                break;
            case ASTDRIVE_STRENGTH:
                drive_str = new ScDrive_strength(c);
                break;
            case ASTDELAY:
                delay = new ScDelay(c);
                break;
            case ASTGATE_INSTANCE:
                gate = new ScGate_instance(c);
                gates.add(gate);
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
