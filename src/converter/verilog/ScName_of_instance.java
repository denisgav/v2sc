package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_instance  <br>
 *     ::=  IDENTIFIER [ range ] 
 */
class ScName_of_instance extends SimpleName {
    ScRange range = null;
    public ScName_of_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_INSTANCE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTRANGE:
                range = new ScRange(c);
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
