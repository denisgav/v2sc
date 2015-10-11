package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> file_declaration ::=
 *   <dd> <b>file</b> identifier_list : subtype_indication [ file_open_information ] ;
 */
class ScFile_declaration extends ScCommonDeclaration {
    public ScFile_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTFILE_DECLARATION);
    }

    public String scString() {
        String ret = intent() + super.scString();
        ret += ";";
        return ret;
    }
}
