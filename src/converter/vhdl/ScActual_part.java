package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> actual_part ::=
 *   <dd> actual_designator
 *   <br> | <i>function_</i>name ( actual_designator )
 *   <br> | type_mark ( actual_designator )
 */
class ScActual_part extends ScVhdl {
    ScFunction_call function_call = null;
    ScType_mark type_mark = null;
    ScActual_designator designator = null;
    public ScActual_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACTUAL_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTFUNCTION_CALL:
                function_call = new ScFunction_call(c);
                break;
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            case ASTACTUAL_DESIGNATOR:
                designator = new ScActual_designator(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(function_call != null) {
            ret += function_call.scString();
        }else if(type_mark != null) {
            ret += type_mark.scString();
            ret += encloseBracket(designator.scString());
        }else {
            ret += designator.scString();
        }
        return ret;
    }
}
