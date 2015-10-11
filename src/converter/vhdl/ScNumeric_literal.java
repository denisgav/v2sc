package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> numeric_literal ::=
 *   <dd> abstract_literal
 *   <br> | physical_literal
 */
class ScNumeric_literal extends ScVhdl {
    ScVhdl literal = null;
    public ScNumeric_literal(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNUMERIC_LITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTABSTRACT_LITERAL:
                literal = new ScAbstract_literal(c);
                break;
            case ASTPHYSICAL_LITERAL:
                literal = new ScPhysical_literal(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return literal.scString();
    }
}
