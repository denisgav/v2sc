package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> secondary_unit_declaration ::=
 *   <dd> identifier = physical_literal ;
 */
class ScSecondary_unit_declaration extends ScVhdl {
    ScVhdl identifier = null;
    ScVhdl phy_literal = null;
    public ScSecondary_unit_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSECONDARY_UNIT_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTPHYSICAL_LITERAL:
                phy_literal = new ScPhysical_literal(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += identifier.scString();
        ret += " = ";
        ret += phy_literal.scString();
        ret += ";";
        return ret;
    }
}
