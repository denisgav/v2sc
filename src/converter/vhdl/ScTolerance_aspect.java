package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> tolerance_aspect ::=
 *   <dd> <b>tolerance</b> <i>string_</i>expression
 */
class ScTolerance_aspect extends ScVhdl {
    public ScTolerance_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTOLERANCE_ASPECT);
    }

    public String scString() {
        return "";
    }
}
