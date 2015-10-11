package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> signature ::=
 *   <dd> [ [ type_mark { , type_mark } ] [ <b>return</b> type_mark ] ]
 */
class ScSignature extends ScVhdl {
    public ScSignature(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGNATURE);
    }

    public String scString() {
        return "";
    }
}
