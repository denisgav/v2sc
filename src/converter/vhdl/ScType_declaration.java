package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> type_declaration ::=
 *   <dd> full_type_declaration
 *   <br> | incomplete_type_declaration
 */
class ScType_declaration extends ScVhdl {
    ScVhdl item = null;
    public ScType_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTFULL_TYPE_DECLARATION:
            item = new ScFull_type_declaration(c);
            break;
        case ASTINCOMPLETE_TYPE_DECLARATION:
            item = new ScIncomplete_type_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.toString();
    }
}
