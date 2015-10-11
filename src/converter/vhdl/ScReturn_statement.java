package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> return_statement ::=
 *   <dd> [ label : ] <b>return</b> [ expression ] ;
 */
class ScReturn_statement extends ScVhdl {
    ScVhdl expression = null;
    public ScReturn_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTRETURN_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += intent() + "return";
        if(expression != null) {
            ret += " " + expression.scString();
        }
        ret += ";";
        return ret;
    }
}
