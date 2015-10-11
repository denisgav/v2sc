package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> indexed_name ::=
 *   <dd> prefix ( expression { , expression } )
 */
class ScIndexed_name extends ScVhdl {
    ScPrefix prefix = null;
    ArrayList<ScExpression> exps = new ArrayList<ScExpression>();
    public ScIndexed_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEXED_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScExpression exp = null;
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new ScPrefix(c);
                break;
            case ASTEXPRESSION:
                exp = new ScExpression(c);
                exps.add(exp);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        String pre = prefix.scString();
        String tmp = "";
        
        ret += pre;
        for(int i = 0; i < exps.size(); i++) {
            tmp += exps.get(i).scString();
            if(i < exps.size() - 1) {
                tmp += ", ";
            }
        }
        ret += encloseBracket(tmp, "[]");
        return ret;
    }
}
