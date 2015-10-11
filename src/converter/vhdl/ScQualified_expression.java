package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> qualified_expression ::=
 *   <dd> type_mark ' ( expression )
 *   <br> | type_mark ' aggregate
 */
class ScQualified_expression extends ScVhdl {
    ScType_mark type_mark = null;
    ScExpression expression = null;
    ScAggregate aggregate = null;
    public ScQualified_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTQUALIFIED_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            case ASTAGGREGATE:
                aggregate = new ScAggregate(c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return type_mark.getBitWidth();
    }

    public String scString() {
        return "";
    }
}
