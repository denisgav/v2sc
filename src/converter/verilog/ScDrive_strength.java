package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  drive_strength  <br>
 *     ::= (  strength0  ,  strength1  ) <br>
 *     ||= (  strength1  ,  strength0  ) 
 */
class ScDrive_strength extends ScVerilog {
    ScVerilog str0 = null;
    ScVerilog str1 = null;
    public ScDrive_strength(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDRIVE_STRENGTH);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTSTRENGTH0:
                if(str0 == null) {
                    str0 = new ScStrength0(c);
                }else {
                    str1 = new ScStrength0(c);
                }
                break;
            case ASTSTRENGTH1:
                if(str0 == null) {
                    str0 = new ScStrength1(c);
                }else {
                    str1 = new ScStrength1(c);
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
