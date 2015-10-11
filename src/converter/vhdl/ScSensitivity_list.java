package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> sensitivity_list ::=
 *   <dd> <i>signal_</i>name { , <i>signal_</i>name }
 */
class ScSensitivity_list extends ScVhdl {
    ArrayList<String> items = new ArrayList<String>();
    public ScSensitivity_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSENSITIVITY_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScName(c);
            items.add(item.scString());
        }
    }
    
    public ArrayList<String> getList() {
        return items;
    }

    public String scString() {
        return "";
    }
}
