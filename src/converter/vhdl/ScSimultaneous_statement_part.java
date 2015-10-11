package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_statement_part ::=
 *   <dd> { simultaneous_statement }
 */
class ScSimultaneous_statement_part extends ScVhdl {
    ArrayList<ScVhdl> statements = new ArrayList<ScVhdl>();
    public ScSimultaneous_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl statement = new ScSimultaneous_statement(c);
            statements.add(statement);
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
