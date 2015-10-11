package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> port_clause ::=
 *   <dd> <b>port</b> ( port_list ) ;
 */
class ScPort_clause extends ScVhdl {
    ScPort_list port_list = null;
    public ScPort_clause(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTPORT_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPORT_LIST:
                port_list = new ScPort_list(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return port_list.scString() + ";" + System.lineSeparator();
    }
}
