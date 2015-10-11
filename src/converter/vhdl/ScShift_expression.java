package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> shift_expression ::=
 *   <dd> simple_expression [ shift_operator simple_expression ]
 */
class ScShift_expression extends ScVhdl {
    ScSimple_expression l_exp = null;
    ScVhdl operator = null;
    ScSimple_expression r_exp = null;
    public ScShift_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSHIFT_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScSimple_expression newNode = null;
            switch(c.getId())
            {
            case ASTSIMPLE_EXPRESSION:
                newNode = new ScSimple_expression(c);
                if(l_exp == null) {
                    l_exp = newNode;
                }else {
                    r_exp = newNode;
                }
                break;
            case ASTVOID:
                operator = new ScShift_operator(c);
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
        super.setLogic(logic);
        if(l_exp != null) {
            l_exp.setLogic(logic);
        }
        if(r_exp != null) {
            r_exp.setLogic(logic);
        }
        if(operator != null) {
            operator.setLogic(logic);
        }
    }

    public String scString() {
        String ret = "";
        String tmp = getReplaceValue(l_exp.scString());
        if(l_exp.items.size() > 2) {
            ret += encloseBracket(tmp);
        }else {
            ret += tmp;
        }

        if(r_exp != null) {
            ret += " " + getReplaceOperator(operator.scString()) + " ";
            tmp = getReplaceValue(r_exp.scString());
            if(r_exp.items.size() > 2) {
                ret += encloseBracket(tmp);
            }else {
                ret += tmp;
            }
        }
        return ret;
    }
}
