package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> waveform_element ::=
 *   <dd> <i>value_</i>expression [ <b>after</b> <i>time_</i>expression ]
 *   <br> | <b>null</b> [ <b>after</b> <i>time_</i>expression ]
 */
class ScWaveform_element extends ScVhdl {
    ScVhdl value_exp = null;
    ScVhdl time_exp = null;
    boolean isNull = false;
    String timeUnit = "";
    public ScWaveform_element(ASTNode node) {
        super(node);
        assert(node.getId() == ASTWAVEFORM_ELEMENT);
        boolean bAfter = false;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl ele = null; 
            switch(c.getId())
            {
            case ASTVOID:
                if(c.firstTokenImage().equalsIgnoreCase(tokenImage[NULL])) {
                    isNull = true;
                }else if(c.firstTokenImage().equalsIgnoreCase(tokenImage[AFTER])) {
                    bAfter = true;
                }
                break;
            case ASTEXPRESSION:
                ele = new ScExpression(c);
                if(!bAfter) {
                    value_exp = ele;
                }else {
                    time_exp = ele;
                    timeUnit = c.getLastToken().image;
                }
                break;
            default:
                break;
            }
        }
    }
    
    public ScVhdl getValue() {
        return value_exp;
    }
    
    public ScVhdl getTime() {
        return time_exp;
    }
    
    public String getTimeUnit() {
        return timeUnit;
    }

    public String scString() {
        String ret = "";
        if(isNull) {
            warning("null ignore");
        }
        return ret;
    }
}
