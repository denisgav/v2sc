package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> association_list ::=
 *   <dd> association_element { , association_element }
 */
class ScAssociation_list extends ScVhdl {
    ArrayList<ScAssociation_element> elements = new ArrayList<ScAssociation_element>();
    public ScAssociation_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSOCIATION_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++){
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTASSOCIATION_ELEMENT);
            ScAssociation_element newNode = new ScAssociation_element(c);
            elements.add(newNode);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < elements.size(); i++) {
            ret += elements.get(i).scString();
            if(i < elements.size() - 1) {
                ret += ", ";
            }
        }
        return ret;
    }
}
