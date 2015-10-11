package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  path_declaration  <br>
 *     ::=  path_description  =  path_delay_value  ; 
 */
class ScPath_declaration extends ScVerilog {
    ScPath_description desc = null;
    ScPath_delay_value delay = null;
    public ScPath_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPATH_DECLARATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTPATH_DESCRIPTION:
                desc = new ScPath_description(c);
                break;
            case ASTPATH_DELAY_VALUE:
                delay = new ScPath_delay_value(c);
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
