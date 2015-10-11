package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> expression ::=
 *   <dd> relation { <b>and</b> relation }
 *   <br> | relation { <b>or</b> relation }
 *   <br> | relation { <b>xor</b> relation }
 *   <br> | relation [ <b>nand</b> relation ]
 *   <br> | relation [ <b>nor</b> relation ]
 *   <br> | relation { <b>xnor</b> relation }
 */
class ScExpression extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScExpression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTRELATION:
                newNode = new ScRelation(c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new ScToken(c);
                items.add(newNode);
                break;
            case ASTLITERAL:
                newNode = new ScLiteral(c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return items.get(0).getBitWidth();
    }
    
    protected void setLogic(boolean logic) {
        super.setLogic(logic);
        for(int i = 0; i < items.size(); i ++) {
            items.get(i).setLogic(logic);
        }
    }

    public String scString() {
        String ret = "";
        ret += items.get(0).scString();
        for(int i = 1; i < items.size() - 1; i += 2){
            ret += getReplaceOperator(items.get(i).scString());
            ret += items.get(i+1).scString();
        }
        return getReplaceValue(ret);
    }
}
