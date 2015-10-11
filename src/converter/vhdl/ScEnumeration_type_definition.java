package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> enumeration_type_definition ::=
 *   <dd> ( enumeration_literal { , enumeration_literal } )
 */
class ScEnumeration_type_definition extends ScCommonIdentifier {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScEnumeration_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENUMERATION_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTENUMERATION_LITERAL:
                newNode = new ScEnumeration_literal(c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent() + "typedef enum " + identifier + " ";
        ret += "{";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += ", ";
            }
        }
        ret += "}";
        return ret;
    }
}
