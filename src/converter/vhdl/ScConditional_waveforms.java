package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> conditional_waveforms ::=
 *   <dd> { waveform <b>when</b> condition <b>else</b> }
 *   <br> waveform [ <b>when</b> condition ]
 */
class ScConditional_waveforms extends ScVhdl {
    ArrayList<CondWaveform> condWaveforms = new ArrayList<CondWaveform>();
    public ScConditional_waveforms(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONDITIONAL_WAVEFORMS);
        CondWaveform cw = null;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTWAVEFORM:
                cw = new CondWaveform();
                cw.waveform = new ScWaveform(c);
                condWaveforms.add(cw);
                break;
            case ASTEXPRESSION:
                cw.condition = new ScCondition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return "";
    }
}
