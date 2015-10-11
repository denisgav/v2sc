package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> process_statement_part ::=
 *   <dd> { sequential_statement }
 */
class ScProcess_statement_part extends ScVhdl {
    ArrayList<ScSequential_statement> statements = new ArrayList<ScSequential_statement>();
    public ScProcess_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCESS_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScSequential_statement s = new ScSequential_statement(c);
            statements.add(s);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < statements.size(); i++) {
            ret += addLF(statements.get(i).toString());
        }
        return ret;
    }
}
