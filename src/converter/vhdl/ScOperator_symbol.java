package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> operator_symbol ::=
 *   <dd> string_literal
 */
class ScOperator_symbol extends ScVhdl {
    ScVhdl string_literal = null;
    public ScOperator_symbol(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOPERATOR_SYMBOL);
        ASTNode c = (ASTNode)node.getChild(0);
        string_literal = new ScString_literal(c);
    }

    public String scString() {
        return string_literal.scString();
    }
}
