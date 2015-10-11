package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> unconstrained_array_definition ::=
 *   <dd> <b>array</b> ( index_subtype_definition { , index_subtype_definition } )
 *   <ul> <b>of</b> <i>element_</i>subtype_indication </ul>
 */
class ScUnconstrained_array_definition extends ScCommonIdentifier {
    ArrayList<ScVhdl> indexItems = new ArrayList<ScVhdl>();
    ScSubtype_indication subtype = null;
    public ScUnconstrained_array_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUNCONSTRAINED_ARRAY_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTINDEX_SUBTYPE_DEFINITION:
                newNode = new ScIndex_subtype_definition(c);
                indexItems.add(newNode);
                break;
            case ASTSUBTYPE_INDICATION:
                subtype = new ScSubtype_indication(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent() + "// Unconstrained_array_definition === ";
        if(indexItems.size() > 0) {
            warning("index_subtype_definition not supported");
        }
        ret += "typedef ";
        ret += subtype.scString();
        ret += " " + identifier;
        
        return ret;
    }
}
