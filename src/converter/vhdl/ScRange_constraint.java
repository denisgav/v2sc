package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> range_constraint ::=
 *   <dd> <b>range</b> range
 */
class ScRange_constraint extends ScVhdl {
    ScRange range = null;
    public ScRange_constraint(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRANGE_CONSTRAINT);
        ASTNode c = (ASTNode)node.getChild(0);
        assert(c.getId() == ASTRANGE);
        range = new ScRange(c);
    }
    
    public String getMin() {
        return range.getMin();
    }
    
    public String getMax() {
        return range.getMax();
    }
    
    public boolean isDownto() {
        return range.isDownto();
    }
    
    public String[] getRange() {
        return range.getRange();
    }

    public String scString() {
        return range.scString();
    }
}
