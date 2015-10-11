package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> binding_indication ::=
 *   <dd> [ <b>use</b> entity_aspect ]
 *   <br> [ generic_map_aspect ]
 *   <br> [ port_map_aspect ]
 *   
 *   <br><br>use with other nodes
 *   @see ScComponent_configuration
 *   @see ScConfiguration_specification
 */
class ScBinding_indication extends ScVhdl {
    ScVhdl entity = null;
    ScVhdl generic_map = null;
    ScVhdl port_map = null;
    public ScBinding_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBINDING_INDICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTENTITY_ASPECT:
                entity = new ScEntity_aspect(c);
                break;
            case ASTGENERIC_MAP_ASPECT:
                generic_map = new ScGeneric_map_aspect(c);
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
        error("binding_indication not support");
        return ret;
    }
}
