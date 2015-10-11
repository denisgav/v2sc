package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> primary_unit ::=
 *   <dd> entity_declaration
 *   <br> | configuration_declaration
 *   <br> | package_declaration
 */
class ScPrimary_unit extends ScVhdl implements IScStatementBlock {
    IScStatementBlock declaration = null;
    public ScPrimary_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPRIMARY_UNIT);
        curEntity = null;
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTENTITY_DECLARATION:
            curEntity = new ScEntity_declaration(c);
            declaration = curEntity;
            break;
        case ASTCONFIGURATION_DECLARATION:
            declaration = new ScConfiguration_declaration(c);
            break;
        case ASTPACKAGE_DECLARATION:
            declaration = new ScPackage_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return declaration.toString();
    }

    @Override
    public String getDeclaration()
    {
        return declaration.getDeclaration();
    }

    @Override
    public String getImplements()
    {
        return declaration.getImplements();
    }

    @Override
    public String getInitCode()
    {
        return declaration.getInitCode();
    }
}
