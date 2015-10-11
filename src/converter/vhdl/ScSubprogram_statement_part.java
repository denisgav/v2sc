package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> subprogram_statement_part ::=
 *   <dd> { sequential_statement }
 */
class ScSubprogram_statement_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScSubprogram_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_STATEMENT_PART);
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
