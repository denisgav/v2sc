package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> parameter_specification ::=
 *   <dd> identifier <b>in</b> discrete_range
 */
class ScParameter_specification extends ScVhdl {
    ScVhdl identifier = null;
    ScDiscrete_range discrete_range = null;
    public ScParameter_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAMETER_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTDISCRETE_RANGE:
                discrete_range = new ScDiscrete_range(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String getMin() {
        return discrete_range.getMin();
    }
    
    public String getMax() {
        return discrete_range.getMax();
    }
    
    public boolean isDownto() {
        return discrete_range.isDownto();
    }

    public String scString() {
        return "";
    }
}
