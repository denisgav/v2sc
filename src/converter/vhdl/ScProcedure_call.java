package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> procedure_call ::=
 *   <dd> <i>procedure_</i>name [ ( actual_parameter_part ) ]
 */
class ScProcedure_call extends ScVhdl {
    ScName name = null;
    ScActual_parameter_part parameter_part = null;
    public ScProcedure_call(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCEDURE_CALL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTASSOCIATION_LIST:
                parameter_part = new ScActual_parameter_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += name.scString();
        if(parameter_part != null) {
            ret += encloseBracket(parameter_part.scString());
        }
        return ret;
    }
}
