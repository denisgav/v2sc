package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   <b>deassign</b>  lvalue  ;
 */
class ScDeassign_lvalue extends ScVerilog {
    ScLvalue lvalue = null;
    public ScDeassign_lvalue(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTDEASSIGN_LVALUE);
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
        String ret = "";
        return ret;
    }
}
