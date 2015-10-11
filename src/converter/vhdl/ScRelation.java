package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> relation ::=
 *   <dd> shift_expression [ relational_operator shift_expression ]
 */
class ScRelation extends ScVhdl {
    ScShift_expression l_exp = null;
    ScVhdl operator = null;
    ScShift_expression r_exp = null;
    public ScRelation(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRELATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScShift_expression newNode = null;
            switch(c.getId())
            {
            case ASTSHIFT_EXPRESSION:
                newNode = new ScShift_expression(c);
                if(l_exp == null) {
                    l_exp = newNode;
                }else {
                    r_exp = newNode;
                }
                break;
            case ASTVOID:
                operator = new ScRelational_operator(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return l_exp.getBitWidth();
    }
    
    protected void setLogic(boolean logic) {
        if(operator != null) {
            logic = true;
            operator.setLogic(true);
        }
        super.setLogic(logic);
        if(r_exp == null) {
            l_exp.setLogic(logic);
        }
    }

    public String scString() {
        String ret = "";
        String tmp = l_exp.scString();
        if(l_exp.r_exp != null) {
            ret = encloseBracket(tmp);
        }else {
            ret = tmp;
        }

        if(r_exp != null) {
            ret += operator.scString();
            tmp = r_exp.scString();
            if(r_exp.r_exp != null) {
                ret += encloseBracket(tmp);
            }else {
                ret += tmp;
            }
            
        }
        return ret;
    }
}
