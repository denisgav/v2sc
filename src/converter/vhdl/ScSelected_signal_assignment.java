package converter.vhdl;

import parser.vhdl.ASTNode;
import java.util.ArrayList;


/**
 * <dl> selected_signal_assignment ::=
 *   <dd> <b>with</b> expression <b>select</b>
 *   <ul> target <= options selected_waveforms ; </ul>
 */
class ScSelected_signal_assignment extends ScVhdl {
    ScExpression expression = null;
    ScTarget target = null;
    ScOptions options = null;
    ScSelected_waveforms selected_waveforms = null;
    public ScSelected_signal_assignment(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSELECTED_SIGNAL_ASSIGNMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            case ASTTARGET:
                target = new ScTarget(c);
                break;
            case ASTOPTIONS:
                options = new ScOptions(c);
                break;
            case ASTSELECTED_WAVEFORMS:
                selected_waveforms = new ScSelected_waveforms(c);
                break;
            default:
                break;
            }
        }
    }
    
    private boolean hasRange() {
        boolean ret = false;
        for(int i = 0; i < selected_waveforms.choicesWaveform.size(); i++) {
            ChoicesWaveform cw = selected_waveforms.choicesWaveform.get(i);
            ScChoices choices = cw.choices;
            if(choices.hasRange()) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    public String scString() {
        String ret = "";
        String val = expression.scString();
        String strTarget = target.scString();
        if(hasRange()) {
            for(int i = 0; i < selected_waveforms.choicesWaveform.size(); i++) {
                ChoicesWaveform cw = selected_waveforms.choicesWaveform.get(i);
                ScChoices choices = cw.choices;
                ArrayList<ScChoice> items = choices.getItems();
                String tmp = "";
                boolean isElse = false;
                for(int j = 0; j < items.size(); j++) {
                    ScChoice choice = items.get(j);
                    if(choice.isRange()) {
                        ScDiscrete_range range = (ScDiscrete_range)choice.item;
                        tmp += "(" + val + " >= " + range.getMin() + " && ";
                        tmp += val + " <= " + range.getMax() + ")";
                    }else if(choice.isOthers()) {
                        isElse = true;
                        break;
                    }else {
                        tmp += val + " == " + choice.scString();
                    }
                    
                    if(j < items.size() - 1) {
                        tmp += " || ";
                    }
                }
                
                if(isElse) {
                    ret += intent() + "else" + System.lineSeparator();
                }else if(i == 0) {
                    ret += intent() + "if(" + tmp + ")";
                }else {
                    ret += intent() + "else if(" + tmp + ")" + System.lineSeparator();
                }

                ret += startIntentBraceBlock();
                ret += addLFIntent(cw.waveform.assignment(strTarget));
                ret += endIntentBraceBlock();
            }
        }else {
            ret += intent() + "switch(" + val + ")" + System.lineSeparator();

            ret += intent() + "{" + System.lineSeparator();
            for(int i = 0; i < selected_waveforms.choicesWaveform.size(); i++) {
                ChoicesWaveform cw = selected_waveforms.choicesWaveform.get(i);
                ScChoices choices = cw.choices;
                for(int j = 0; j < choices.items.size(); j++) {
                    ScChoice item = choices.items.get(j);
                    if(item.isOthers()){
                        ret += intent() + "default:" + System.lineSeparator();
                    }else {
                        ret += intent() + "case " + item.scString();
                        ret += ":" + System.lineSeparator();
                    }
                }
                startIntentBlock();
                ret += cw.waveform.assignment(strTarget) + ";" + System.lineSeparator();
                ret += intent() + "break;" + System.lineSeparator();
                endIntentBlock();
            }
            ret += intent() + "}" + System.lineSeparator();
        }
        return ret;
    }
}
