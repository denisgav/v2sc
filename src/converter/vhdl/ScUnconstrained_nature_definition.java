package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> unconstrained_nature_definition ::=
 *   <dd> <b>array</b> ( index_subtype_definition { , index_subtype_definition } )
 *   <ul> <b>of</b> subnature_indication </ul>
 */
class ScUnconstrained_nature_definition extends ScVhdl {
    public ScUnconstrained_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUNCONSTRAINED_NATURE_DEFINITION);
    }

    public String scString() {
        return "";
    }
}
