package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> through_aspect ::=
 *   <dd> identifier_list [ tolerance_aspect ] [ := expression ] <b>through</b>
 */
class ScThrough_aspect extends ScVhdl {
    public ScThrough_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTHROUGH_ASPECT);
    }

    public String scString() {
        return "";
    }
}
