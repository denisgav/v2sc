package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  expandrange  <br>
 *     ::=  range  <br>
 *     ||= <b>scalared</b>  range  <br>
 *     ||= <b>vectored</b>  range  
 */
class ScExpandrange extends ScVerilog {
    ScRange range = null;
    /** 0--simple range, 1--scalared range, 2--vectored range */
    int type = 0;
    public ScExpandrange(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEXPANDRANGE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTRANGE:
                range = new ScRange(c);
                break;
            case ASTVERILOG_TOKEN:
                if(c.firstTokenImage().equals(SCALARED)) {
                    type = 1;
                }else {
                    type = 2;
                }
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
