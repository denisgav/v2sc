package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> alias_indication ::=
 *   <dd> subtype_indication
 *   <br> | subnature_indication
 */
class ScAlias_indication extends ScVhdl {
    ScVhdl subNode = null;
    public ScAlias_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALIAS_INDICATION);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTSUBTYPE_INDICATION:
            subNode = new ScSubtype_indication(c);
            break;
        case ASTSUBNATURE_INDICATION:
            subNode = new ScSubnature_indication(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return subNode.scString();
    }
}
