package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> logical_name_list ::=
 *   <dd> logical_name { , logical_name }
 */
class ScLogical_name_list extends ScVhdl {
    ArrayList<ScVhdl> names = new ArrayList<ScVhdl>();
    public ScLogical_name_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLOGICAL_NAME_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                newNode = new ScLogical_name(c);
                names.add(newNode);
                break;
            default:
                break;
            }
        }
    }
    
    public ArrayList<ScVhdl> getNames() {
        return names;
    }

    public String scString() {
        return "";
    }
}
