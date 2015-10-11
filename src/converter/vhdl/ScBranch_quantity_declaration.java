package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> branch_quantity_declaration ::=
 *   <dd> <b>quantity</b> [ across_aspect ] [ through_aspect ] terminal_aspect ;
 */
class ScBranch_quantity_declaration extends ScVhdl {
    ScVhdl across = null;
    ScVhdl through = null;
    ScVhdl terminal = null;
    public ScBranch_quantity_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTBRANCH_QUANTITY_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTACROSS_ASPECT:
                across = new ScAcross_aspect(c);
                break;
            case ASTTHROUGH_ASPECT:
                through = new ScThrough_aspect(c);
                break;
            case ASTTERMINAL_ASPECT:
                terminal = new ScTerminal_aspect(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        error();
        return "";
    }
}
