package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> generation_scheme ::=
 *   <dd> <b>for</b> <i>generate_</i>parameter_specification
 *   <br> | <b>if</b> condition
 */
class ScGeneration_scheme extends ScVhdl {
    ScParameter_specification param = null;
    ScCondition condition = null;
    public ScGeneration_scheme(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERATION_SCHEME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPARAMETER_SPECIFICATION:
                param = new ScParameter_specification(c);
                break;
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent();
        if(condition != null) {
            ret += "if(" + condition.scString() + ")";
        }else {
            String var = param.identifier.scString();
            ret += "for(int " + var + " = " + param.getMin() + "; ";
            ret += var + " < " + addOne(param.getMax()) + "; ";
            ret += var + "++)";
        }
        return ret;
    }
}
