package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> iteration_scheme ::=
 *   <dd> <b>while</b> condition
 *   <br> | <b>for</b> <i>loop_</i>parameter_specification
 */
class ScIteration_scheme extends ScVhdl {
    ScCondition condition = null;
    ScParameter_specification param = null;
    public ScIteration_scheme(ASTNode node) {
        super(node);
        assert(node.getId() == ASTITERATION_SCHEME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            case ASTPARAMETER_SPECIFICATION:
                param = new ScParameter_specification(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(condition != null) {
            ret += "while(" + condition.scString() + ")";
        }else {
            ret += "for(";
            String var = param.identifier.scString();
            if(param.isDownto()) {
                ret += var + " = " + param.getMax() + "; ";
                ret += var + " >= " + param.getMin() + "; ";
                ret += var + "--";
            }else {
                ret += var + " = " + param.getMin() + "; ";
                ret += var + " < " + addOne(param.getMax()) + "; ";
                ret += var + "++";
            }
            ret += ")";
        }
        return ret;
    }
}
