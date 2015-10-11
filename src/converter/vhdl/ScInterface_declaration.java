package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> interface_declaration ::=
 *   <dd> interface_constant_declaration
 *   <br> | interface_signal_declaration
 *   <br> | interface_variable_declaration
 *   <br> | interface_file_declaration
 *   <br> | interface_terminal_declaration
 *   <br> | interface_quantity_declaration
 */
class ScInterface_declaration extends ScVhdl {
    ScCommonDeclaration item = null;
    public ScInterface_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTINTERFACE_CONSTANT_DECLARATION:
            item = new ScInterface_constant_declaration(c);
            break;
        case ASTINTERFACE_SIGNAL_DECLARATION:
            item = new ScInterface_signal_declaration(c);
            break;
        case ASTINTERFACE_VARIABLE_DECLARATION:
            item = new ScInterface_variable_declaration(c);
            break;
        case ASTINTERFACE_FILE_DECLARATION:
            item = new ScInterface_file_declaration(c);
            break;
        case ASTINTERFACE_TERMINAL_DECLARATION:
            item = new ScInterface_terminal_declaration(c);
            break;
        case ASTINTERFACE_QUANTITY_DECLARATION:
            item = new ScInterface_quantity_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}
