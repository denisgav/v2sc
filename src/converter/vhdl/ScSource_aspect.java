package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> source_aspect ::=
 *   <dd> <b>spectrum</b> <i>magnitude_</i>simple_expression , <i>phase_</i>simple_expression
 *   <br> | <b>noise</b> <i>power_</i>simple_expression
 */
class ScSource_aspect extends ScVhdl {
    public ScSource_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSOURCE_ASPECT);
    }

    public String scString() {
        return "";
    }
}
