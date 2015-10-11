package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> file_open_information ::=
 *   <dd> [ <b>open</b> <i>file_open_kind_</i>expression ] <b>is</b> file_logical_name
 */
class ScFile_open_information extends ScVhdl {
    public ScFile_open_information(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFILE_OPEN_INFORMATION);
    }

    public String scString() {
        return "";
    }
}
