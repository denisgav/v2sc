package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> label ::=
 *   <dd> identifier
 */
class ScLabel extends ScCommonIdentifier {
    public ScLabel(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTLABEL);
        identifier = node.firstTokenImage();
    }
}
