package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> range ::=
 *   <dd> <i>range_</i>attribute_name
 *   <br> | simple_expression direction simple_expression
 */
class ScRange extends ScVhdl {
    ScAttribute_name attribute_name = null;
    ScVhdl simple_exp1 = null;
    ScDirection direction = null;
    ScVhdl simple_exp2 = null;
    public ScRange(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRANGE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTATTRIBUTE_NAME:
                attribute_name = new ScAttribute_name(c);
                break;
            case ASTSIMPLE_EXPRESSION:
                newNode = new ScSimple_expression(c);
                if(simple_exp1 == null) {
                    simple_exp1 = newNode;
                }else {
                    simple_exp2 = newNode;
                }
                break;
            case ASTDIRECTION:
                direction = new ScDirection(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return getWidth(simple_exp1.scString(), simple_exp2.scString());
    }
    
    public String getMin() {
        String ret = "0";
        if(attribute_name == null) {
            if(direction.dir.equalsIgnoreCase(RANGE_TO)) {
                ret = simple_exp1.scString();
            }else {
                ret = simple_exp2.scString();
            }
        }else {
            attribute_name.getMin();
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(attribute_name != null) {
            ret = attribute_name.getMax();
        }else {
            if(direction.dir.equalsIgnoreCase(RANGE_TO)) {
                ret = simple_exp2.scString();
            }else {
                ret = simple_exp1.scString();
            }
        }
        return ret;
    }
    
    public boolean isDownto() {
        boolean ret = false;
        if(direction != null &&
                direction.dir.equalsIgnoreCase(RANGE_DOWNTO)) {
            ret = true;
        }
        return ret;
    }
    
    public String[] getRange() {
        if(attribute_name != null) {
            return attribute_name.getRange();
        }
        String[] ret = {
                simple_exp1.scString(), 
                direction.scString(), 
                simple_exp2.scString()
            };
        return ret;
    }

    public String scString() {
        String ret = "";
        if(simple_exp1 != null) {
            ret += simple_exp1.scString();
            ret += ", ";
            ret += simple_exp2.scString();
        }
        return ret;
    }
}
