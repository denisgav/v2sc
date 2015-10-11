package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> generic_clause ::=
 *   <dd> <b>generic</b> ( generic_list ) ;
 */
class ScGeneric_clause extends ScVhdl {
    ScGeneric_list generic_list = null;
    public ScGeneric_clause(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTGENERIC_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTGENERIC_LIST:
                generic_list = new ScGeneric_list(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return generic_list.toString();
    }
}
