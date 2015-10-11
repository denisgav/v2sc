package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> subnature_indication ::=
 *   <dd> nature_mark [ index_constraint ] [ <b>tolerance</b> <i>string_</i>expression <b>across</b> <i>string_</i>expression <b>through</b> ]
 */
class ScSubnature_indication extends ScVhdl {
    public ScSubnature_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBNATURE_INDICATION);
    }

    public String scString() {
        return "";
    }
}
