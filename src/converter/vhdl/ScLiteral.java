package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> literal ::=
 *   <dd> numeric_literal
 *   <br> | enumeration_literal
 *   <br> | string_literal
 *   <br> | bit_string_literal
 *   <br> | <b>null</b>
 */
class ScLiteral extends ScVhdl {
    ScVhdl item = null;
    public ScLiteral(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int kind  = 0;
            switch(c.getId())
            {
            case ASTENUMERATION_LITERAL:
                item = new ScEnumeration_literal(c);
                break;
            case ASTNUMERIC_LITERAL:
                item = new ScNumeric_literal(c);
                break;
            case ASTVOID:
                kind = c.getFirstToken().kind;
                switch(kind)
                {
                case NULL:
                    item = new ScToken(c);    // TODO convert null
                    break;
                case string_literal:
                    item = new ScString_literal(c);
                    break;
                case bit_string_literal:
                    item = new ScBit_string_literal(c);
                    break;
                default:
                    break;
                }
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }
}
