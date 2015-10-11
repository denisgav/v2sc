package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> type_definition ::=
 *   <dd> scalar_type_definition
 *   <br> | composite_type_definition
 *   <br> | access_type_definition
 *   <br> | file_type_definition
 */
class ScType_definition extends ScCommonIdentifier {
    ScCommonIdentifier item = null;
    public ScType_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSCALAR_TYPE_DEFINITION:
            item = new ScScalar_type_definition(c);
            break;
        case ASTCOMPOSITE_TYPE_DEFINITION:
            item = new ScComposite_type_definition(c);
            break;
        case ASTACCESS_TYPE_DEFINITION:
            item = new ScAccess_type_definition(c);
            break;
        case ASTFILE_TYPE_DEFINITION:
            item = new ScFile_type_definition(c);
            break;
        default:
            break;
        }
    }
    
    public void setIdentifier(String ident) {
        super.setIdentifier(ident);
        item.setIdentifier(ident);
    }

    public String scString() {
        return item.scString();
    }
}
