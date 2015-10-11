package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> slice_name ::=
 *   <dd> prefix ( discrete_range )
 */
class ScSlice_name extends ScVhdl {
    ScPrefix prefix = null;
    ScDiscrete_range range = null;
    public ScSlice_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSLICE_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new ScPrefix(c);
                break;
            case ASTDISCRETE_RANGE:
                range = new ScDiscrete_range(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return range.getBitWidth();
    }

    public String scString() {
        String ret = "";
        if(!curNode.isDescendantOf(ASTEXPRESSION)
            && !curNode.isDescendantOf(ASTTARGET)
            && !curNode.isDescendantOf(ASTPORT_MAP_ASPECT)
            && !curNode.isDescendantOf(ASTPROCEDURE_CALL)
            && !curNode.isDescendantOf(ASTFUNCTION_CALL)) {
            ret += getReplaceType(prefix.scString(), range.getRange());
        }else {
            ret += prefix.scString() + ".range(";
            ret += range.scString();
            ret += ")";
        }
        return ret;
    }
}
