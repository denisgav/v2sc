package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   <b>release</b>  lvalue  ; 
 */
class ScRelease_statement extends ScVerilog {
    ScLvalue lvalue = null;
    public ScRelease_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTRELEASE_LVALUE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTLVALUE:
                lvalue = new ScLvalue(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return lvalue.toString() + ";" + System.lineSeparator();
    }
}
