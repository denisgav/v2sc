package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> configuration_declaration ::=
 *   <dd> <b>configuration</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
 *   <ul> configuration_declarative_part
 *   <br> block_configuration
 *   </ul><b>end</b> [ <b>configuration</b> ] [ <i>configuration_</i>simple_name ] ;
 */
class ScConfiguration_declaration extends ScCommonIdentifier implements IScStatementBlock {
    ScName entity_name = null;
    ScConfiguration_declarative_part declarative_part = null;
    ScBlock_configuration block_config = null;
    public ScConfiguration_declaration(ASTNode node) {
        super(node, true);
        int i = 0;
        assert(node.getId() == ASTCONFIGURATION_DECLARATION);
        for(i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                break;
            case ASTNAME:
                entity_name = new ScName(c);
                break;
            case ASTCONFIGURATION_DECLARATIVE_PART:
                declarative_part = new ScConfiguration_declarative_part(c);
                break;
            case ASTBLOCK_CONFIGURATION:
                block_config = new ScBlock_configuration(c);
                break;
            default:
                break;
            }
        }
        
        assert(entity_name != null);
        for(i = 0; i < units.size(); i++) {
            ScCommonIdentifier ident = units.get(i);
            if(ident instanceof ScEntity_declaration
                && ident.identifier.equalsIgnoreCase(entity_name.scString())) {
                ((ScEntity_declaration)ident).setConfigurationDeclaration(this);
                break;
            }
        }
        if(i == units.size()) {
            warning("configuration declaration has no corresponding entity");
        }
    }

    public String scString() {
        return "";
    }

    @Override
    public String getDeclaration()
    {
        String ret = "";
        ret += addPrevComment();
        warning("configuration not support");
        return ret;
    }

    @Override
    public String getImplements()
    {
        warning("configuration not support");
        return "";
    }

    @Override
    public String getInitCode()
    {
        warning("configuration not support");
        return "";
    }
}
