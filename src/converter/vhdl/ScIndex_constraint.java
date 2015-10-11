package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> index_constraint ::=
 *   <dd> ( discrete_range { , discrete_range } )
 */
class ScIndex_constraint extends ScVhdl {
    ArrayList<ScDiscrete_range> ranges = new ArrayList<ScDiscrete_range>();
    public ScIndex_constraint(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEX_CONSTRAINT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScDiscrete_range range = null;
            switch(c.getId())
            {
            case ASTDISCRETE_RANGE:
                range = new ScDiscrete_range(c);
                ranges.add(range);
                break;
            default:
                break;
            }
        }
    }

    public String getMin() {
        String ret = "0";
        if(ranges.size() > 0) {
            ret = ((ScDiscrete_range)ranges.get(0)).getMin();   //TODO modify here
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(ranges.size() > 0) {
            ret = ((ScDiscrete_range)ranges.get(0)).getMax();   //TODO modify here
        }
        return ret;
    }
    
    public boolean isDownto() {
        boolean ret = true;
        if(ranges.size() > 0) {
            ret = ((ScDiscrete_range)ranges.get(0)).isDownto(); //TODO modify here
        }
        return ret;
    }
    
    public String[] getRange() {
        String[] ret = null;
        if(ranges.size() > 0) {
            ret = ((ScDiscrete_range)ranges.get(0)).getRange(); //TODO modify here
        }
        return ret;
    }
    
    public String scString() {
        String ret = "";
        if(ranges.size() > 1) {
            warning("multi-range not support");
        }
        ranges.get(0).scString();
        return ret;
    }
}
