package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> selected_waveforms ::=
 *   <dd> { waveform <b>when</b> choices , }
 *   <br> waveform <b>when</b> choices
 */
class ScSelected_waveforms extends ScVhdl {
    ArrayList<ChoicesWaveform> choicesWaveform = new ArrayList<ChoicesWaveform>();
    public ScSelected_waveforms(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSELECTED_WAVEFORMS);
        ChoicesWaveform cw = null;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTWAVEFORM:
                cw = new ChoicesWaveform();
                choicesWaveform.add(cw);
                cw.waveform = new ScWaveform(c);
                break;
            case ASTCHOICES:
                cw.choices = new ScChoices(c);
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
