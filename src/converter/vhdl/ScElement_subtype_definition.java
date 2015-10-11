package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> element_subtype_definition ::=
 *   <dd> subtype_indication
 */
class ScElement_subtype_definition extends ScVhdl {
    ScSubtype_indication item = null;
    public ScElement_subtype_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTELEMENT_SUBTYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBTYPE_INDICATION:
                item = new ScSubtype_indication(c);
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
