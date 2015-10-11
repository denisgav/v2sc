package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> library_unit ::=
 *   <dd> primary_unit
 *   <br> | secondary_unit
 */
class ScLibrary_unit extends ScVhdl implements IScStatementBlock {
    ScVhdl unit = null;
    public ScLibrary_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIBRARY_UNIT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTPRIMARY_UNIT:
            unit = new ScPrimary_unit(c);
            break;
        case ASTSECONDARY_UNIT:
            unit = new ScSecondary_unit(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return unit.scString();
    }

    @Override
    public String getDeclaration()
    {
        if(unit instanceof ScPrimary_unit)
            return ((ScPrimary_unit)unit).getDeclaration();
        else
            return "";
    }

    @Override
    public String getImplements()
    {
        if(unit instanceof ScPrimary_unit)
            return ((ScPrimary_unit)unit).getImplements();
        else
            return "";
    }

    @Override
    public String getInitCode()
    {
        if(unit instanceof ScPrimary_unit)
            return ((ScPrimary_unit)unit).getInitCode();
        else
            return "";
    }
}
