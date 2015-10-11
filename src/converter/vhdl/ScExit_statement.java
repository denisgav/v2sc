package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> exit_statement ::=
 *   <dd> [ label : ] <b>exit</b> [ <i>loop_</i>label ] [ <b>when</b> condition ] ;
 */
class ScExit_statement extends ScVhdl {
    ScCondition condition = null;
    public ScExit_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTEXIT_STATEMENT);
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
            ret += intent(curLevel+1) + "break;";  //FIXME: may break in switch
        }else {
            ret += intent() + "break;";
        }
        if(curNode.getParent().getId() == ASTCASE_STATEMENT_ALTERNATIVE) {
            warning("break switch statement instead of loop!");
        }
        return ret;
    }
}
