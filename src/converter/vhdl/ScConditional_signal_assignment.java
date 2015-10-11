package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> conditional_signal_assignment ::=
 *   <dd> target <= options conditional_waveforms ;
 */
class ScConditional_signal_assignment extends ScVhdl {
    ScTarget target = null;
    ScOptions options = null;
    ScConditional_waveforms waveforms = null;
    public ScConditional_signal_assignment(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCONDITIONAL_SIGNAL_ASSIGNMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTARGET:
                target = new ScTarget(c);
                break;
            case ASTOPTIONS:
                options = new ScOptions(c);
                break;
            case ASTCONDITIONAL_WAVEFORMS:
                waveforms = new ScConditional_waveforms(c);
                break;
            default:
                break;
            }
        }
    }
    
    public boolean hasCondition() {
        for(int i = 0; i < waveforms.condWaveforms.size(); i++) {
            CondWaveform cw = waveforms.condWaveforms.get(i);
            if(cw.condition != null) {
                return true;
            }
        }
        return false;
    }

    public String scString() {
        String ret = "";
        String val = target.scString();
        if(options != null && options.curNode.getChildrenNum() > 0) {
            warning("options ignored");
        }
        
        if(!hasCondition()) {
            CondWaveform cw = waveforms.condWaveforms.get(0);
            ret += cw.waveform.assignment(val) + ";" + System.lineSeparator();
        }else {
            for(int i = 0; i < waveforms.condWaveforms.size(); i++) {
                CondWaveform cw = waveforms.condWaveforms.get(i);
                ScCondition condition = cw.condition;
                if(i == 0) {
                    ret += intent() + "if(" + condition.scString() + ")" + System.lineSeparator();
                }else if(condition != null) {
                    ret += intent() + "else if(" + condition.scString() + ")" + System.lineSeparator();
                }else {
                    ret += intent() + "else" + System.lineSeparator();
                }
                ret += startIntentBraceBlock();
                ret += cw.waveform.assignment(val) + ";" + System.lineSeparator();
                ret += endIntentBraceBlock();
            }
        }

        return ret;
    }
}
