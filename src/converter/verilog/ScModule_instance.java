package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  module_instance  <br>
 *     ::=  name_of_instance  ( [ list_of_module_connections ] ) 
 */
class ScModule_instance extends ScVerilog {
    ScName_of_instance name = null;
    ScList_of_module_connections connect = null;
    public ScModule_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE_INSTANCE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNAME_OF_INSTANCE:
                name = new ScName_of_instance(c);
                break;
            case ASTLIST_OF_MODULE_CONNECTIONS:
                connect = new ScList_of_module_connections(c);
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
