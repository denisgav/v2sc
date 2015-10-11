package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> null_statement ::=
 *   <dd> [ label : ] <b>null</b> ;
 */
class ScNull_statement extends ScVhdl {
    public ScNull_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTNULL_STATEMENT);
    }

    public String scString() {
        warning("null statement ignore");
        return "";
    }
}
