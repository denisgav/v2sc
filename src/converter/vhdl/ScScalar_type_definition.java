package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> scalar_type_definition ::=
 *   <dd> enumeration_type_definition
 *   <br> | integer_type_definition
 *   <br> | floating_type_definition
 *   <br> | physical_type_definition
 */
class ScScalar_type_definition extends ScCommonIdentifier {
    ScCommonIdentifier item = null;
    public ScScalar_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSCALAR_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTENUMERATION_TYPE_DEFINITION:
            item = new ScEnumeration_type_definition(c);
            break;
        case ASTINTEGER_TYPE_DEFINITION:    // the same as float type
            item = new ScInteger_type_definition(c);
            break;
        case ASTPHYSICAL_TYPE_DEFINITION:
            item = new ScPhysical_type_definition(c);
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
