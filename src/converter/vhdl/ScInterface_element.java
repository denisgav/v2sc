package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> interface_element ::=
 *   <dd> interface_declaration
 */
class ScInterface_element extends ScVhdl {
    ScInterface_declaration item = null;
    public ScInterface_element(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTINTERFACE_ELEMENT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTINTERFACE_DECLARATION:
            item = new ScInterface_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}
