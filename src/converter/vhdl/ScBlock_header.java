package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> block_header ::=
 *   <dd> [ generic_clause
 *   <br>   [ generic_map_aspect ; ] ]
 *   <br> [ port_clause
 *   <br>   [ port_map_aspect ; ] ]
 */
class ScBlock_header extends ScVhdl {
    ScGeneric_clause generic = null;
    ScGeneric_map_aspect generic_map = null;
    ScPort_clause port = null;
    ScPort_map_aspect port_map = null;
    public ScBlock_header(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_HEADER);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTGENERIC_CLAUSE:
                generic = new ScGeneric_clause(c);
                break;
            case ASTGENERIC_MAP_ASPECT:
                generic_map = new ScGeneric_map_aspect(c);
                break;
            case ASTPORT_CLAUSE:
                port = new ScPort_clause(c);
                break;
            case ASTPORT_MAP_ASPECT:
                port_map = new ScPort_map_aspect(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(generic != null) {
            ret += generic.toString();
            if(generic_map != null) {
                ret += System.lineSeparator() + generic_map.toString();
            }
        }
        if(!ret.isEmpty()) {
            ret += System.lineSeparator();
        }
        if(port != null) {
            ret += port.toString();
            if(port_map != null) {
                ret += System.lineSeparator() + port.toString();
            }
        }
        return ret;
    }
}
