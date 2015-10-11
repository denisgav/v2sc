package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> break_list ::=
 *   <dd> break_element { , break_element }
 */
class ScBreak_list extends ScVhdl {
    ArrayList<ScVhdl> elements = new ArrayList<ScVhdl>();
    public ScBreak_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBREAK_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTBREAK_ELEMENT);
            ScVhdl ele = new ScBreak_element(c);
            elements.add(ele);
        }
    }

    public String scString() {
        error();
        return "";
    }
}
