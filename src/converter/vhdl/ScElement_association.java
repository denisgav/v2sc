package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> element_association ::=
 *   <dd> [ choices => ] expression
 */
class ScElement_association extends ScVhdl {
    ScChoices choices = null;
    ScExpression expression = null;
    public ScElement_association(ASTNode node) {
        super(node);
        assert(node.getId() == ASTELEMENT_ASSOCIATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCHOICES:
                choices = new ScChoices(c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        if(choices != null && choices.isOthers()) {
            return 1;
        }
        return expression.getBitWidth();
    }
    
    protected void setLogic(boolean logic) {
        super.setLogic(logic);
        if(choices == null)
            expression.setLogic(logic);
    }
    
    public String orgString() {
        String ret = "";
        if(choices != null)
            ret += choices.scString() + " => ";
        ret += getReplaceValue(expression.scString());
        return ret;
    }
    
    public String toBitString(int max, boolean isArray) {
        String ret = "";
        String val = expression.scString();
        if(choices != null) {
            //TODO choices
            if(choices.isOthers()) {
                if(isArray) {
                    for(int i = 0; i < max; i++) {
                        ret += getReplaceValue(val);
                        if(i < max-1) { ret += ", "; }
                    }
                }else {
                    String tmp = getReplaceValue(val);
                    if(max == 1)
                        ret += tmp;
                    else {
                        ret += '\"';
                        for(int i = 0; i < max; i++) { ret += tmp; }
                        ret += '\"';
                    }
                }
            }else {
                ret += val;
            }
        }else {
            ret += val;
        }
        return ret;
    }

    public String scString() {
        return toBitString(getBitWidth(), false);
    }
}
