package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> array_type_definition ::=
 *   <dd> unconstrained_array_definition | constrained_array_definition
 */
class ScArray_type_definition extends ScCommonIdentifier {
    ScCommonIdentifier item = null;
    public ScArray_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARRAY_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTUNCONSTRAINED_ARRAY_DEFINITION:
            item = new ScUnconstrained_array_definition(c);
            break;
        case ASTCONSTRAINED_ARRAY_DEFINITION:
            item = new ScConstrained_array_definition(c);
            break;
        default:
            break;
        }
    }
    
    public void setIdentifier(String ident) {
        super.setIdentifier(ident);
        item.setIdentifier(ident);
    }

    public String scString() {
        return item.scString();
    }
}
