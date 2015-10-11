package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_statement ::=
 *   <dd> simple_simultaneous_statement
 *   <br> | simultaneous_if_statement
 *   <br> | simultaneous_case_statement
 *   <br> | simultaneous_procedural_statement
 *   <br> | simultaneous_null_statement
 */
class ScSimultaneous_statement extends ScVhdl implements IScStatementBlock {
    IScStatementBlock item = null;
    public ScSimultaneous_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSIMPLE_SIMULTANEOUS_STATEMENT:
                item = new ScSimple_simultaneous_statement(c);
                break;
            case ASTSIMULTANEOUS_IF_STATEMENT:
                item = new ScSimultaneous_if_statement(c);
                break;
            case ASTSIMULTANEOUS_CASE_STATEMENT:
                item = new ScSimultaneous_case_statement(c);
                break;
            case ASTSIMULTANEOUS_PROCEDURAL_STATEMENT:
                item = new ScSimultaneous_procedural_statement(c);
                break;
            case ASTSIMULTANEOUS_NULL_STATEMENT:
                item = new ScSimultaneous_null_statement(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }

    @Override
    public String getDeclaration() {
        return item.getDeclaration();
    }

    @Override
    public String getImplements() {
        return item.getImplements();
    }

    @Override
    public String getInitCode()
    {
        return item.getInitCode();
    }
}
