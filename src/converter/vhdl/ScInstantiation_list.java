package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> instantiation_list ::=
 *   <dd> <i>instantiation_</i>label { , <i>instantiation_</i>label }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class ScInstantiation_list extends ScVhdl {
    public ScInstantiation_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINSTANTIATION_LIST);
    }

    public String scString() {
        return "";
    }
}
