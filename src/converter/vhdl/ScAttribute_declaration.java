package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> attribute_declaration ::=
 *   <dd> <b>attribute</b> identifier : type_mark ;
 */
class ScAttribute_declaration extends ScVhdl {
    ScIdentifier identifier = null;
    ScType_mark type_mark = null;
    public ScAttribute_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTATTRIBUTE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        error("user defined attribute not support!");
        return "";
    }
}
