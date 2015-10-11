package converter.vhdl;

import parser.vhdl.ASTNode;
import java.util.ArrayList;


/**
 * <dl> library_clause ::=
 *   <dd> <b>library</b> logical_name_list ;
 */
class ScLibrary_clause extends ScVhdl {
    ScLogical_name_list logical_name_list = null;
    public ScLibrary_clause(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTLIBRARY_CLAUSE);
        logical_name_list = new ScLogical_name_list((ASTNode)node.getChild(0));
    }
    
    public ArrayList<ScVhdl> getNames() {
        return logical_name_list.getNames();
    } 

    public String scString() {
        return "";
        //return logical_name_list.scString() + ";";
    }
}
