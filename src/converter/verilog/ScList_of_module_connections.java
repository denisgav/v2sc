package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  list_of_module_connections  <br>
 *     ::=  module_port_connection  {, module_port_connection } <br>
 *     ||=  named_port_connection  {, named_port_connection } 
 */
class ScList_of_module_connections extends ScVerilog {
    ArrayList<ScVerilog> ports = new ArrayList<ScVerilog>();
    public ScList_of_module_connections(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_MODULE_CONNECTIONS);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScVerilog item = null; 
            switch(c.getId())
            {
            case ASTNAMED_PORT_CONNECTION:
                item = new ScNamed_port_connection(c);
                ports.add(item);
                break;
            case ASTMODULE_PORT_CONNECTION:
                item = new ScModule_port_connection(c);
                ports.add(item);
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
