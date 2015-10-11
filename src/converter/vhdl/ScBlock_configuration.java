package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> block_configuration ::=
 *   <dd> <b>for</b> block_specification
 *   <ul> { use_clause }
 *   <br> { configuration_item }
 *   </ul> <b>end</b> <b>for</b> ;
 *   <br><br>
 *   @see ScComponent_configuration
 *   @see ScConfiguration_declaration
 *   @see ScConfiguration_item
 */
class ScBlock_configuration extends ScVhdl {
    ScVhdl spec = null;
    ScVhdl use_clause = null;
    ScVhdl cfg_item = null;
    public ScBlock_configuration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTBLOCK_CONFIGURATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCK_SPECIFICATION:
                spec = new ScBlock_specification(c);
                break;
            case ASTUSE_CLAUSE:
                use_clause = new ScUse_clause(c);
                break;
            case ASTCONFIGURATION_ITEM:
                cfg_item = new ScConfiguration_item(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        error();
        return ret;
    }
}
