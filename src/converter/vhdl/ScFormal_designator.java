package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> formal_designator ::=
 *   <dd> <i>generic_</i>name
 *   <br> | <i>port_</i>name
 *   <br> | <i>parameter_</i>name
 */
class ScFormal_designator extends ScVhdl {
    ScName name = null;
    public ScFormal_designator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFORMAL_DESIGNATOR);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return name.scString();
    }
}
