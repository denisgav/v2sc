package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> options ::=
 *   <dd> [ <b>guarded</b> ] [ delay_mechanism ]
 */
class ScOptions extends ScVhdl {
    ScDelay_mechanism delay_mechanism = null;
    public ScOptions(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOPTIONS);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTDELAY_MECHANISM:
                delay_mechanism = new ScDelay_mechanism(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return delay_mechanism.scString();
    }
}
