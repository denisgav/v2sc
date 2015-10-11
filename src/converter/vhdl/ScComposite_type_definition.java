package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> composite_type_definition ::=
 *   <dd> array_type_definition
 *   <br> | record_type_definition
 */
class ScComposite_type_definition extends ScCommonIdentifier {
    ScCommonIdentifier item = null;
    public ScComposite_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPOSITE_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTARRAY_TYPE_DEFINITION:
            item = new ScArray_type_definition(c);
            break;
        case ASTRECORD_TYPE_DEFINITION:
            item = new ScRecord_type_definition(c);
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
