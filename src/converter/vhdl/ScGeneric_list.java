package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> generic_list ::=
 *   <dd> <i>generic_</i>interface_list
 */
class ScGeneric_list extends ScVhdl {
    ScInterface_list list = null;
    public ScGeneric_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERIC_LIST);
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
        String ret = "";
        for(int i = 0; i < list.items.size(); i++) {
            ret += list.items.get(i).addPrevComment();
            ret += intent() + list.items.get(i).scString();
            if(i < list.items.size() - 1) {
                ret += ",";
            }
            ret += list.items.get(i).addPostComment();
            if(i < list.items.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }
}
