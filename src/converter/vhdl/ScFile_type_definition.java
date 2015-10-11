package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> file_type_definition ::=
 *   <dd> <b>file of</b> type_mark
 */
class ScFile_type_definition extends ScCommonIdentifier {
    ScType_mark type_mark = null;
    public ScFile_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFILE_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        warning("token file ignored");
        return intent() + type_mark.scString() + " " + identifier;
    }
}
