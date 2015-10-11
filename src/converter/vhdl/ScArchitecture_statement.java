package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> architecture_statement ::=
 *   <dd> simultaneous_statement
 *   <br> | concurrent_statement
 */
class ScArchitecture_statement extends ScVhdl implements IScStatementBlock {
    IScStatementBlock statement = null;
    public ScArchitecture_statement(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTARCHITECTURE_STATEMENT);
        switch(node.getId())
        {
        case ASTSIMULTANEOUS_STATEMENT:
            statement = new ScSimultaneous_statement(node);
            break;
        case ASTCONCURRENT_STATEMENT:
            statement = new ScConcurrent_statement(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return statement.scString();
    }

    @Override
    public String getDeclaration() {
        return statement.getDeclaration();
    }

    @Override
    public String getImplements() {
        return statement.getImplements();
    }

    @Override
    public String getInitCode()
    {
        return statement.getInitCode();
    }
    
    protected void setStatementPart(ScArchitecture_statement_part st) {
        if(statement instanceof ScConcurrent_statement) {
          ((ScConcurrent_statement)statement).setStatementPart(st);
        }
    }
}
