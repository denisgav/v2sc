package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> index_specification ::=
 *   <dd> discrete_range
 *   <br> | <i>static_</i>expression
 */
class ScIndex_specification extends ScVhdl {
    ScVhdl item = null;
    public ScIndex_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEX_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTDISCRETE_RANGE:
                item = new ScDiscrete_range(c);
                break;
            case ASTEXPRESSION:
                item = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += item.scString();
        return ret;
    }
}
