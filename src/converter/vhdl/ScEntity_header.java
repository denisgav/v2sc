package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_header ::=
 *   <dd> [ <i>formal_</i>generic_clause ]
 *   <br> [ <i>formal_</i>port_clause ]
 */
class ScEntity_header extends ScVhdl {
    ScGeneric_clause generic = null;
    ScPort_clause port = null;
    public ScEntity_header(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_HEADER);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTGENERIC_CLAUSE:
                generic = new ScGeneric_clause(c);
                break;
            case ASTPORT_CLAUSE:
                port = new ScPort_clause(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String scString() {
        String ret = "";
        if(port != null) {
            ret += port.toString();
        }
        return ret;
    }
}
