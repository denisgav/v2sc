package converter.vhdl;

import parser.vhdl.ASTNode;
import parser.IASTNode;


/**
 * <dl> across_aspect ::=
 *   <dd> identifier_list
 *   [ tolerance_aspect ]
 *   [ := expression ] <b>across</b>
 */
class ScAcross_aspect extends ScVhdl {
    ScVhdl idlist = null;
    ScVhdl tolerance_aspect = null;
    ScVhdl expression = null;
    public ScAcross_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACROSS_ASPECT);
        int i;
        for(i = 0; i < node.getChildrenNum(); i++) {
            IASTNode c = node.getChild(i);
            int id = c.getId();
            switch(id)
            {
            case ASTIDENTIFIER_LIST:
                idlist = new ScIdentifier_list((ASTNode)c);
                break;
            case ASTTOLERANCE_ASPECT:
                tolerance_aspect = new ScTolerance_aspect((ASTNode)c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression((ASTNode)c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        assert(idlist != null);
        String ret = idlist.scString();
        if(tolerance_aspect != null) {
            warning("tolerance_aspect ignored");
            ret += tolerance_aspect.scString();
        }
        if(expression != null) {
            ret += " = ";
            ret += expression.scString();
        }
        warning("token across ignored");
        return ret;
    }
}
