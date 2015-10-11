package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> simple_name ::=
 *   <dd> identifier
 */
class ScSimple_name extends ScCommonIdentifier {
    public ScSimple_name(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSIMPLE_NAME);
        assert(node.getId() == ASTIDENTIFIER);
        identifier = node.firstTokenImage();
    }
}
