package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> instantiated_unit ::=
 *   <dd> [ <b>component</b> ] <i>component_</i>name
 *   <br> | <b>entity</b> <i>entity_</i>name [ ( <i>architecture_</i>identifier ) ]
 *   <br> | <b>configuration</b> <i>configuration_</i>name
 */
class ScInstantiated_unit extends ScVhdl {
    ScName name = null;
    public ScInstantiated_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINSTANTIATED_UNIT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return "";
    }
}
