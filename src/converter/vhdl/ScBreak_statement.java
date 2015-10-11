package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> break_statement ::=
 *   <dd> [ label : ] <b>break</b> [ break_list ] [ <b>when</b> condition ] ;
 */
class ScBreak_statement extends ScVhdl {
    ScBreak_list break_list = null;
    ScCondition condition = null;
    public ScBreak_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTBREAK_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            case ASTBREAK_LIST:
                break_list = new ScBreak_list(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        error();
        return "";
    }
}
