package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> entity_statement_part ::=
 *   <dd> { entity_statement }
 */
class ScEntity_statement_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScEntity_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScEntity_statement(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
            if(i < items.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }
}
