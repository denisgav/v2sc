package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> adding_operator ::=
 *   <dd> + | - | &
 */
class ScAdding_operator extends ScVhdl {
    ScToken token = null;
    boolean isConcat = false;
    public ScAdding_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTADDING_OPERATOR);
        token = new ScToken(node);
        isConcat = token.image.equals("&");
    }

    public String scString() {
        if(isConcat)
            return ",";
        else
            return token.scString();
    }
}
