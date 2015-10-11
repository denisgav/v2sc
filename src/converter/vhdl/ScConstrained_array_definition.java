package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> constrained_array_definition ::=
 *   <dd> <b>array</b> index_constraint <b>of</b> <i>element_</i>subtype_indication
 */
class ScConstrained_array_definition extends ScCommonIdentifier {
    ScIndex_constraint index_constraint = null;
    ScSubtype_indication subtype = null;
    public ScConstrained_array_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTRAINED_ARRAY_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTINDEX_CONSTRAINT:
                index_constraint = new ScIndex_constraint(c);
                break;
            case ASTSUBTYPE_INDICATION:
                subtype = new ScSubtype_indication(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent() + "typedef ";
        ret += subtype.scString();
        ret += " " + identifier;
        ret += encloseBracket(addOne(index_constraint.getMax()), "[]");
        return ret;
    }
}
