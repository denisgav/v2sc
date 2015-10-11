package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> array_nature_definition ::=
 *   <dd> unconstrained_nature_definition | constrained_nature_definition
 */
class ScArray_nature_definition extends ScVhdl {
    ScVhdl itemNode = null;
    public ScArray_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARRAY_NATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTUNCONSTRAINED_NATURE_DEFINITION:
                itemNode = new ScUnconstrained_nature_definition(c);
                break;
            case ASTCONSTRAINED_NATURE_DEFINITION:
                itemNode = new ScConstrained_nature_definition(c);
                break;
            default:
                break;
            }            
        }

    }

    public String scString() {
        return itemNode.scString();
    }
}
