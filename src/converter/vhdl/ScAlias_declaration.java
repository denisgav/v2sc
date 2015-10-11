package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> alias_declaration ::=
 *   <dd> <b>alias</b> alias_designator [ : alias_indication ] <b>is</b> name [ signature ] ;
 */
class ScAlias_declaration extends ScVhdl {
    ScVhdl designator = null;
    ScVhdl indication = null;
    ScVhdl name = null;
    ScVhdl signature = null;
    public ScAlias_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTALIAS_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int id = c.getId();
            switch(id)
            {
            case ASTALIAS_DESIGNATOR:
                designator = new ScAlias_designator(c);
                break;
            case ASTALIAS_INDICATION:
                indication = new ScAlias_indication(c);
                break;
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTSIGNATURE:
                signature = new ScSignature(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent() + "define ";
        ret += designator.scString();
        if(indication != null) {
            warning("alias indication ignored");
        }
        ret += " " + name.scString();
        if(signature != null) {
            warning("alias signature ignored");
        }
        return ret;
    }
}
