package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> block_statement_part ::=
 *   <dd> { architecture_statement }
 */
class ScBlock_statement_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScBlock_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScArchitecture_statement(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).toString();
            if(i < items.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }
}
