package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> scalar_nature_definition ::=
 *   <dd> type_mark <b>across</b>
 *   <dd> type_mark <b>through</b>
 *   <dd> identifier <b>reference</b>
 */
class ScScalar_nature_definition extends ScVhdl {
    public ScScalar_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSCALAR_NATURE_DEFINITION);
    }

    public String scString() {
        return "";
    }
}
