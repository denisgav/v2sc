package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  description  <br>
 *     ::=  module  <br>
 *     ||=  udp  
 */
class ScDescription extends ScVerilog {
    ScVerilog module = null;
    public ScDescription(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDESCRIPTION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTMODULE:
                module = new ScModule(c);
                break;
            case ASTUDP:
                module = new ScUdp(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return module.toString();
    }
}
