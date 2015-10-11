package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  terminal  <br>
 *     ::=  expression  <br>
 *     ||=  IDENTIFIER  
 */
class ScTerminal extends ScVerilog {
    ScVerilog item = null;
    public ScTerminal(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTERMINAL);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                item = new ScIDENTIFIER0(c);
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
