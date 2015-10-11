package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> subprogram_specification ::=
 *   <dd> <b>procedure</b> designator [ ( formal_parameter_list ) ]
 *   <br> | [ <b>pure</b> | <b>impure</b> ] <b>function</b> designator [ ( formal_parameter_list ) ]
 *   <ul> <b>return</b> type_mark </ul>
 */
class ScSubprogram_specification extends ScVhdl {
    boolean isFunction = false;
    ScVhdl designator = null;
    ScVhdl parameter_list = null;
    ScVhdl type_mark = null;
    public ScSubprogram_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTDESIGNATOR:
                designator = new ScDesignator(c);
                break;
            case ASTVOID:
                if(c.firstTokenImage().equalsIgnoreCase(tokenImage[PROCEDURE])) {
                    isFunction = false;
                }else {
                    isFunction = true;
                }
                break;
            case ASTFORMAL_PARAMETER_LIST:
                parameter_list = new ScFormal_parameter_list(c);
                break;
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                isFunction = true;
                break;
            default:
                break;
            }
        }
    }
    
    public String specString(boolean isBody) {
        String ret = intent();
        if(type_mark != null) {     // type_mark appear only in function
            ret += type_mark.scString() + " ";
        }else {
            ret += "void ";
        }
        if(isBody && curIndividual) {
            ret += className + "::";
        }
        ret += designator.scString();
        if(parameter_list != null) {
            ret += encloseBracket(parameter_list.scString());
        } else {
            ret += "()";
        }
        return ret;
    }

    public String scString() {
        return specString(false);
    }
}
