package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> assertion_statement ::=
 *   <dd> [ label : ] assertion ;
 */
class ScAssertion_statement extends ScVhdl {
    ScVhdl label = null;
    ScVhdl assertion = null;
    public ScAssertion_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTASSERTION_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                label = new ScIdentifier(c);
                break;
            case ASTASSERTION:
                assertion = new ScAssertion(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent();
        if(label != null) {
            warning("label " + label.scString() + " ignored");
        }
        ret += assertion.scString() + ";";
        return ret;
    }
}
