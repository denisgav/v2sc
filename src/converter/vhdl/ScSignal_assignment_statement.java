package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> signal_assignment_statement ::=
 *   <dd> [ label : ] target <= [ delay_mechanism ] waveform ;
 */
class ScSignal_assignment_statement extends ScVhdl {
    ScTarget target = null;
    ScWaveform waveform = null;
    ScDelay_mechanism delay = null;
    public ScSignal_assignment_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSIGNAL_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTARGET:
                target = new ScTarget(c);
                break;
            case ASTDELAY_MECHANISM:
                delay = new ScDelay_mechanism(c);
                break;
            case ASTWAVEFORM:
                waveform = new ScWaveform(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent();
        ret = waveform.assignment(target.scString());
        ret += ";";
        return ret;
    }
}
