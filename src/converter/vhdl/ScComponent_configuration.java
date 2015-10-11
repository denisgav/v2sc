package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> component_configuration ::=
 *   <dd> <b>for</b> component_specification
 *   <ul> [ binding_indication ; ]
 *   <br> [ block_configuration ]
 *   </ul> <b>end</b> <b>for</b> ;
 */
class ScComponent_configuration extends ScVhdl {
    ScVhdl spec = null;
    ScVhdl binding = null;
    ScVhdl block = null;
    public ScComponent_configuration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCOMPONENT_CONFIGURATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCOMPONENT_SPECIFICATION:
                spec = new ScComponent_specification(c);
                break;
            case ASTBINDING_INDICATION:
                binding = new ScBinding_indication(c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                block = new ScBlock_configuration(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        warning("component_configuration not support");
        return "";
    }
}
