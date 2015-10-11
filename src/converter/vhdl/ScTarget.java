package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> target ::=
 *   <dd> name
 *   <br> | aggregate
 */
class ScTarget extends ScVhdl {
    ScVhdl item = null;
    public ScTarget(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTARGET);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTNAME:
            item = new ScName(c);
            break;
        case ASTAGGREGATE:
            item = new ScAggregate(c);
            break;
        default:
            break;
        }
    }
    
    public String[] getTargetRange() {
        String[] ret = null;
        if(item instanceof ScName) {
            ScName name = (ScName)item;
            if(name.item instanceof ScSlice_name) {
                ret = ((ScSlice_name)name.item).range.getRange();
            }else {
                String[] segs = null;
                segs = name.getNameSegments();
                ret = getTypeRange(item.curNode, segs);
            }
        }else {
            warning("aggregate target range not supported");
            //TODO aggregate target range
            ret = new String[3];
            ret[0] = "0";
            ret[1] = tokenImage[TO];
            ret[2] = String.format("%d", item.getBitWidth());
        }
        return ret;
    }
    
    public String[] getTargetArrayRange() {
        String[] ret = null;
        if(item instanceof ScName) {
            String[] segs = ((ScName)item).getNameSegments();
            ret = getArrayRange(item.curNode, segs);
        }else {
            warning("aggregate target range not supported");
            //TODO aggregate target range
        }
        return ret;
    }
    
    public String scString() {
        return item.scString();
    }
}
