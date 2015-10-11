package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> procedure_call_statement ::=
 *   <dd> [ label : ] procedure_call ;
 */
class ScProcedure_call_statement extends ScVhdl {
    ScVhdl procedure_call = null;
    public ScProcedure_call_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTPROCEDURE_CALL_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPROCEDURE_CALL:
                procedure_call = new ScProcedure_call(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent();
        ret += procedure_call.scString();
        ret += ";";
        return ret;
    }
}
