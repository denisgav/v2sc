package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> terminal_aspect ::=
 *   <dd> <i>plus_terminal_</i>name [ <b>to</b> <i>minus_terminal_</i>name ]
 */
class ScTerminal_aspect extends ScVhdl {
    public ScTerminal_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTERMINAL_ASPECT);
    }

    public String scString() {
        return "";
    }
}
