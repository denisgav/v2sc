package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> enumeration_literal ::=
 *   <dd> identifier | character_literal
 */
class ScEnumeration_literal extends ScVhdl {
    ScVhdl item = null;
    public ScEnumeration_literal(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENUMERATION_LITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                item = new ScIdentifier(c);
                break;
            case ASTVOID:
                item = new ScCharacter_literal(c);
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
