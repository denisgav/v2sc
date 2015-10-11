package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> next_statement ::=
 *   <dd> [ label : ] <b>next</b> [ <i>loop_</i>label ] [ <b>when</b> condition ] ;
 */
class ScNext_statement extends ScVhdl {
    ScCondition condition = null;
    public ScNext_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTNEXT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(condition != null) {
            ret += intent() + "if(" + condition.scString() + ")" + System.lineSeparator();
            ret += intent(curLevel+1) + "continue;";
        }else {
            ret += intent() + "continue;";
        }
        return ret;
    }
}
