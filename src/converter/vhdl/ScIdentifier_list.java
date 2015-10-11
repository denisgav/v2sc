package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> identifier_list ::=
 *   <dd> identifier { , identifier }
 */
class ScIdentifier_list extends ScVhdl {
    ArrayList<ScIdentifier> items = new ArrayList<ScIdentifier>();
    public ScIdentifier_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTIDENTIFIER_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTIDENTIFIER);
            ScIdentifier item = new ScIdentifier(c);
            items.add(item);
        }
    }
    
    public ArrayList<ScIdentifier> getItems() {
        return items;
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
            if(i < items.size() - 1) {
                ret += ", ";
            }
        }
        return ret;
    }
}
