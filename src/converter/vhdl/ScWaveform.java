package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> waveform ::=
 *   <dd> waveform_element { , waveform_element }
 *   <br> <b>| unaffected</b>
 */
class ScWaveform extends ScVhdl {
    ArrayList<ScWaveform_element> elements = new ArrayList<ScWaveform_element>();
    boolean isUnaffected = false;
    public ScWaveform(ASTNode node) {
        super(node);
        assert(node.getId() == ASTWAVEFORM);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScWaveform_element ele = null; 
            switch(c.getId())
            {
            case ASTVOID:   // unaffected
                isUnaffected = true;
                break;
            case ASTWAVEFORM_ELEMENT:
                ele = new ScWaveform_element(c);
                elements.add(ele);
                break;
            default:
                break;
            }
        }
    }
    
    public String assignment(String target) {
        String ret = "";
        if(isUnaffected) {
            warning("unaffected ignore");
            return "";
        }
            
        for(int j = 0; j < elements.size(); j++) {
            ScWaveform_element ele = elements.get(j);
            ScExpression delayTime = (ScExpression)ele.getTime();
            if(delayTime != null) {
                String unit = ele.getTimeUnit();
                ret += intent() + "wait(" + delayTime.scString() + 
                                ", " + getSCTime(unit) + ");" + System.lineSeparator();                
            }
            if(!ele.isNull) {
                ret += intent() + target + ".write(" + ele.getValue().scString() + ")";
            }
            if(j < elements.size() - 1) {
                ret += ";" + System.lineSeparator();
            }
        }
        return ret;
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
