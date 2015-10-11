package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> object_declaration ::=
 *   <dd> constant_declaration
 *   <br> | signal_declaration
 *   <br> | variable_declaration
 *   <br> | file_declaration
 *   <br> | terminal_declaration
 *   <br> | quantity_declaration
 */
class ScObject_declaration extends ScVhdl {
    public ScObject_declaration(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTOBJECT_DECLARATION);
        // no use
    }

    public String scString() {
        return "";
    }
}
