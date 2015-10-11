package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> interface_file_declaration ::=
 *   <dd> <b>file</b> identifier_list : subtype_indication
 */
class ScInterface_file_declaration extends ScCommonDeclaration {

    public ScInterface_file_declaration(ASTNode node) {
        super(node, false);
        assert(node.getId() == ASTINTERFACE_FILE_DECLARATION);
    }

    public String scString() {
        String ret = intent() + "file ";
        ret += super.scString();
        return ret;
    }
}
