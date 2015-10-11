package converter.vhdl;

import parser.vhdl.ASTNode;
import java.util.ArrayList;


/**
 * <dl> sensitivity_clause ::=
 *   <dd> <b>on</b> sensitivity_list
 */
class ScSensitivity_clause extends ScVhdl {
    ScSensitivity_list sensitivity_list = null;
    public ScSensitivity_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSENSITIVITY_CLAUSE);
        sensitivity_list = new ScSensitivity_list((ASTNode)node.getChild(0));
    }

    public String scString() {
        return "";
    }
    
    ArrayList<String> getSensitiveList() {
        return sensitivity_list.getList();
    }
}
