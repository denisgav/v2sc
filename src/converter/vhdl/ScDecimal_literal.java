package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> decimal_literal ::=
 *   <dd> integer [ . integer ] [ exponent ]
 */
class ScDecimal_literal extends ScVhdl {
    String literal = "";
    public ScDecimal_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTDECIMAL_LITERAL);
        literal = node.firstTokenImage();
    }

    public String scString() {
        return literal;
    }
}
