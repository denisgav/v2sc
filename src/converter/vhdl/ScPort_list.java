package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> port_list ::=
 *   <dd> <i>port_</i>interface_list
 */
class ScPort_list extends ScVhdl {
    ScInterface_list list = null;
    public ScPort_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTINTERFACE_LIST:
                list = new ScInterface_list(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return list.scString();
    }
}
