package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> choices ::=
 *   <dd> choice { | choice }
 */
class ScChoices extends ScVhdl {
    ArrayList<ScChoice> items = new ArrayList<ScChoice>();
    public ScChoices(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCHOICES);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ScChoice item = new ScChoice((ASTNode)node.getChild(i));
            items.add(item);
        }
    }
    
    public boolean hasRange() {
        boolean ret = false;
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).isRange()) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    public boolean isOthers() {
        for(int i = 0; i < items.size(); i++) {
            ScChoice item = items.get(i);
            if(item.isOthers()) {
                return true;
            }
        }
        return false;
    }
    
    ArrayList<ScChoice> getItems() {
        return items;
    }

    public String scString() {
        String ret = "";
        if(items.size() > 1)
            ret += "(";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += ", ";
            }
        }
        if(items.size() > 1)
            ret += ")";
        return ret;
    }
}
