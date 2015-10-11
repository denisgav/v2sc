package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> physical_literal ::=
 *   <dd> [ abstract_literal ] <i>unit_</i>name
 */
class ScPhysical_literal extends ScVhdl {
    ScVhdl abstract_literal = null;
    String time_unit_name = "ns";
    public ScPhysical_literal(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPHYSICAL_LITERAL);
        abstract_literal = new ScAbstract_literal((ASTNode)node.getChild(0));
        time_unit_name = node.getLastToken().image;
    }
    
    public String getTimeUnitName() {
        return time_unit_name;
    }

    public String scString() {
        return abstract_literal.scString();
    }
}
