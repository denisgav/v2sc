package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> nature_mark ::=
 *   <dd> <i>nature_</i>name | <i>subnature_</i>name
 */
class ScNature_mark extends ScVhdl {
    public ScNature_mark(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNATURE_MARK);
    }

    public String scString() {
        return "";
    }
}
