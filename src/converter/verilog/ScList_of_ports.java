package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  list_of_ports  <br>
 *     ::= (  port  {, port } ) 
 */
class ScList_of_ports extends ScVerilog {
    ArrayList<ScPort> ports = new ArrayList<ScPort>(); 
    public ScList_of_ports(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_PORTS);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScPort port = null;
            switch(c.getId())
            {
            case ASTPORT:
                port = new ScPort(c);
                ports.add(port);
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
