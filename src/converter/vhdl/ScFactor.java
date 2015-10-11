package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> factor ::=
 *   <dd> primary [ ** primary ]
 *   <br> | <b>abs</b> primary
 *   <br> | <b>not</b> primary
 */
class ScFactor extends ScVhdl {
    ScPrimary primary0 = null;
    ScVhdl operator = null;
    ScPrimary primary1 = null;
    public ScFactor(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFACTOR);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScPrimary newNode = null;
            switch(c.getId())
            {
            case ASTPRIMARY:
                newNode = new ScPrimary(c);
                if(primary0 == null) {
                    primary0 = newNode;
                }else {
                    primary1 = newNode;
                }
                break;
            case ASTVOID:
                operator = new ScMiscellaneous_operator(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return primary0.getBitWidth();
    }
    
    protected void setLogic(boolean logic) {
        super.setLogic(logic);
        if(primary1 == null)
            primary0.setLogic(logic);
    }

    public String scString() {
        String ret = "";
        if(operator != null
            && operator.scString().equalsIgnoreCase(vhdlOperators[VHDL_ABS])) {
            String tmp = getReplaceOperator(operator.scString());
            ret += tmp + encloseBracket(primary0.scString());
        }else if(operator != null
            && operator.scString().equalsIgnoreCase(vhdlOperators[VHDL_NOT])) {
            String tmp = getReplaceOperator(operator.scString());
            ret += tmp + primary0.scString();
        }else {
            if(primary1 == null) {
                ret += primary0.scString();
            }else {
                ret += "(";
                ret += primary0.scString();
                ret += getReplaceOperator(operator.scString());
                ret += primary1.scString();
                ret += ")";
            }
        }
        return ret;
    }
}
