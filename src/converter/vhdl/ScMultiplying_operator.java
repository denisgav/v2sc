package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> multiplying_operator ::=
 *   <dd> * | / | <b>mod</b> | <b>rem</b>
 */
class ScMultiplying_operator extends ScVhdl {
    String token = "";
    public ScMultiplying_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTMULTIPLYING_OPERATOR);
        token = node.firstTokenImage(); 
    }

    public String scString() {
        return getReplaceOperator(token);
    }
}
