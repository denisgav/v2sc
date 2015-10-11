package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> constraint ::=
 *   <dd> range_constraint
 *   <br> | index_constraint
 */
class ScConstraint extends ScVhdl {
    ScRange_constraint range = null;
    ScIndex_constraint index = null;
    public ScConstraint(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTRAINT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE_CONSTRAINT:
            range = new ScRange_constraint(c);
            break;
        case ASTINDEX_CONSTRAINT:
            index = new ScIndex_constraint(c);
            break;
        default:
            break;
        }
    }
    
    public String getMin() {
        String ret = "0";
        if(range != null) {
            ret = range.getMin();
        }else {
            ret = index.getMin();
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(range != null) {
            ret = range.getMax();
        }else {
            ret = index.getMax();
        }
        return ret;
    }
    
    public boolean isDownto() {
        boolean ret = false;
        if(range != null) {
            ret = range.isDownto();
        }else {
            ret = index.isDownto();
        }
        return ret;
    }
    
    public String[] getTypeRange() {
        if(index != null) {
            return index.getRange();
        }else {
            return null;
        }
    }
    
    public String[] getValueRange() {
        if(range != null) {
            return range.getRange();
        }else {
            return null;
        }
    }
    
    public String[] getRange() {
        String[] ret = null;
        if(range != null) {
            ret = range.getRange();
        }else {
            ret = index.getRange();
        }
        return ret;
    }

    public String scString() {
        return "";
    }
}
