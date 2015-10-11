package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> integer_type_definition ::=
 *   <dd> range_constraint
 */
class ScInteger_type_definition extends ScCommonIdentifier {
    ScRange_constraint range = null;
    public ScInteger_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTEGER_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE_CONSTRAINT:
            range = new ScRange_constraint(c);
            break;
        default:
            break;
        }
    }
    
    public String getMin() {
        return range.getMin();
    }
    
    public String getMax() {
        return range.getMax();
    }

    public String scString() {
        String ret = intent() + "typedef ";
        ret += "sc_uint<" + addOne(getMax()) + "> ";
        ret += identifier;
        return ret;
    }
}
