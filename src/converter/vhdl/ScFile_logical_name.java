package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> file_logical_name ::=
 *   <dd> <i>string_</i>expression
 */
class ScFile_logical_name extends ScVhdl {
    public ScFile_logical_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFILE_LOGICAL_NAME);
    }

    public String scString() {
        return "";
    }
}
