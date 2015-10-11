package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  initial_statement  <br>
 *     ::= <b>initial</b>  statement  
 */
class ScInitial_statement extends ScVerilog {
    ScStatement statement = null;
    public ScInitial_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINITIAL_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTSTATEMENT:
                statement = new ScStatement(c);
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
