package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> element_subnature_definition ::=
 *   <dd> subnature_indication
 */
class ScElement_subnature_definition extends ScVhdl {
    ScSubnature_indication item = null;
    public ScElement_subnature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTELEMENT_SUBNATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBNATURE_INDICATION:
                item = new ScSubnature_indication(c);
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
