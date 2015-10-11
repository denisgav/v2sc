package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> primary_unit_declaration ::=
 *   <dd> identifier ;
 */
class ScPrimary_unit_declaration extends ScVhdl {
    ScVhdl identifier = null;
    public ScPrimary_unit_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTPRIMARY_UNIT_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        assert(c.getId() == ASTIDENTIFIER);
        identifier = new ScIdentifier(c);
    }

    public String scString() {
        String ret = "";
        ret += identifier.scString();
        ret += ";";
        return ret;
    }
}
