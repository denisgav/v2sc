package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> identifier ::=
 *   <dd> basic_identifier | extended_identifier
 */
class ScIdentifier extends ScCommonIdentifier {
    public ScIdentifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTIDENTIFIER);
        identifier = node.firstTokenImage();
    }
}
