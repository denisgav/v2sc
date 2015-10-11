package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> designator ::=
 *   <dd> identifier | operator_symbol
 */
class ScDesignator extends ScVhdl {
    String name = "";
    public ScDesignator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDESIGNATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        name = c.firstTokenImage();
    }

    public String scString() {
        return name;
    }
}
