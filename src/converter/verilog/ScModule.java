package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  module  <br>
 *     ::= <b>module</b>  name_of_module  [ list_of_ports ] ; <br>
 *         { module_item } <br>
 *         <b>endmodule</b> <br>
 *     ||= <b>macromodule</b>  name_of_module  [ list_of_ports ] ; <br>
 *         { module_item } <br>
 *         <b>endmodule</b> 
 */
class ScModule extends ScVerilog {
    ScName_of_module name = null;
    ScList_of_ports ports = null;
    ArrayList<ScParameter_declaration> params = new ArrayList<ScParameter_declaration>();
    ArrayList<ScModule_item> items = new ArrayList<ScModule_item>();
    public ScModule(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScModule_item item = null;
            switch(c.getId())
            {
            case ASTNAME_OF_MODULE:
                name = new ScName_of_module(c);
                break;
            case ASTLIST_OF_PORTS:
                ports = new ScList_of_ports(c);
                break;
            case ASTMODULE_ITEM:
                item = new ScModule_item(c);
                if(item.isParameter()) {
                    params.add((ScParameter_declaration)item.item);
                }else {
                    items.add(item);
                }
                items.add(item);
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
