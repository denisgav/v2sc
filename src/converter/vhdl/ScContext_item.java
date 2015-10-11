package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> context_item ::=
 *   <dd> library_clause
 *   <br> | use_clause
 */
class ScContext_item extends ScVhdl {
    ScVhdl item = null;
    boolean isUse = false;
    public ScContext_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTCONTEXT_ITEM);
        switch(node.getId())
        {
        case ASTLIBRARY_CLAUSE:
            item = new ScLibrary_clause(node);
            break;
        case ASTUSE_CLAUSE:
            item = new ScUse_clause(node);
            isUse = true;
            break;
        default:
            break;
        }
    }

    public String scString() {
        String ret = "";
        ret += item.toString();
        return ret;
    }
}
