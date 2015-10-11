package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> full_type_declaration ::=
 *   <dd> <b>type</b> identifier <b>is</b> type_definition ;
 */
class ScFull_type_declaration extends ScVhdl {
    ScIdentifier identifier = null;
    ScType_definition type_def = null;
    public ScFull_type_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTFULL_TYPE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTTYPE_DEFINITION:
                type_def = new ScType_definition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        type_def.setIdentifier(identifier.scString());
        ret += type_def.scString();
        ret += ";" + System.lineSeparator();
        return ret;
    }
}
