package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> break_element ::=
 *   <dd> [ break_selector_clause ] <i>quantity_</i>name => expression
 */
class ScBreak_element extends ScVhdl {
    ScVhdl selector = null;
    ScVhdl name = null;
    ScVhdl expression = null;
    public ScBreak_element(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBREAK_ELEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBREAK_SELECTOR_CLAUSE:
                selector = new ScBreak_selector_clause(c);
                break;
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression(c);
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
