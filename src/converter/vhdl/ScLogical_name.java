package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> logical_name ::=
 *   <dd> identifier
 */
class ScLogical_name extends ScVhdl {
    ScVhdl identifier = null;
    public ScLogical_name(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTLOGICAL_NAME);
        identifier = new ScIdentifier(node);
    }

    public String scString() {
        return identifier.scString();
    }
}
