package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> break_selector_clause ::=
 *   <dd> <b>for</b> <i>quantity_</i>name <b>use</b>
 */
class ScBreak_selector_clause extends ScVhdl {
    ScName name = null;
    public ScBreak_selector_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBREAK_SELECTOR_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
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
