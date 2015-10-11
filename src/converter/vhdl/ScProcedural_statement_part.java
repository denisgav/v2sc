package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> procedural_statement_part ::=
 *   <dd> { sequential_statement }
 */
class ScProcedural_statement_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScProcedural_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCEDURAL_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScSequential_statement(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += addLF(items.get(i).toString());
        }
        return ret;
    }
}
