package converter.vhdl;

import converter.IScModule;
import converter.IncludePath;
import parser.vhdl.ASTNode;


/**
 * <dl> design_unit ::=
 *   <dd> context_clause library_unit
 */
class ScDesign_unit extends ScVhdl implements IScModule {
    ScContext_clause context_clause = null;
    ScLibrary_unit library_unit = null;
    public ScDesign_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDESIGN_UNIT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCONTEXT_CLAUSE:
                context_clause = new ScContext_clause(c);
                break;
            case ASTLIBRARY_UNIT:
                library_unit = new ScLibrary_unit(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += context_clause.toString();
        ret += System.lineSeparator();
        ret += library_unit.toString();
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        return library_unit.getDeclaration();
    }

    @Override
    public String getImplements()
    {
        return library_unit.getImplements();
    }

    @Override
    public IncludePath[] getInclude()
    {
        return context_clause.getInclude();
    }
}
