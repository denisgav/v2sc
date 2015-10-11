package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> sequence_of_statements ::=
 *   <dd> { sequential_statement }
 */
class ScSequence_of_statements extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScSequence_of_statements(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSEQUENCE_OF_STATEMENTS);
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
