package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  module_port_connection  <br>
 *     ::=  expression  <br>
 *     ||=  NULL  
 */
class ScModule_port_connection extends ScVerilog {
    ScVerilog item = null;
    public ScModule_port_connection(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE_PORT_CONNECTION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNULL:
                item = new ScNULL(c);
                break;
            case ASTEXPRESSION:
                item = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }
}
