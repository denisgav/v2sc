package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> constrained_nature_definition ::=
 *   <dd> <b>array</b> index_constraint <b>of</b> subnature_indication
 */
class ScConstrained_nature_definition extends ScVhdl {
    public ScConstrained_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTRAINED_NATURE_DEFINITION);
    }

    public String scString() {
        return "";
    }
}
