package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> allocator ::=
 *   <dd> <b>new</b> subtype_indication
 *   <br> | <b>new</b> qualified_expression
 */
class ScAllocator extends ScVhdl {
    ScVhdl subNode = null;
    public ScAllocator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALLOCATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTSUBTYPE_INDICATION:
            subNode = new ScSubtype_indication(c);
            break;
        case ASTQUALIFIED_EXPRESSION:
            subNode = new ScQualified_expression(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return subNode.scString();
    }
}
