package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> primary ::=
 *   <dd> name
 *   <br> | literal
 *   <br> | aggregate
 *   <br> | function_call
 *   <br> | qualified_expression
 *   <br> | type_conversion
 *   <br> | allocator
 *   <br> | ( expression )
 */
class ScPrimary extends ScVhdl {
    ScVhdl item = null;
    public ScPrimary(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPRIMARY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                item = new ScName(c);
                break;
            case ASTFUNCTION_CALL:
                item = new ScFunction_call(c);
                break;
            case ASTTYPE_CONVERSION:
                item = new ScType_conversion(c);
                break;
            case ASTLITERAL:
                item = new ScLiteral(c);
                break;
            case ASTAGGREGATE:
                item = new ScAggregate(c);
                break;
            case ASTEXPRESSION:
                item = new ScExpression(c);
                break;
            case ASTQUALIFIED_EXPRESSION:
                item = new ScQualified_expression(c);
                break;
            case ASTALLOCATOR:
                item = new ScAllocator(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return item.getBitWidth();
    }
    
    protected void setLogic(boolean logic) {
        super.setLogic(logic);
        item.setLogic(logic);
    }

    public String scString() {
        if(item instanceof ScExpression) {
            return encloseBracket(item.scString());
        }else {
            return item.scString();
        }
    }
}
