package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  always_statement  <br>
 *     ::= <b>always</b>  statement  
 */
class ScAlways_statement extends ScVerilog {
    ScStatement statement = null;
    public ScAlways_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALWAYS_STATEMENT);
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
        return statement.scString();
    }
}
