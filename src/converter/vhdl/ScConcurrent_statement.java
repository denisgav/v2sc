package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> concurrent_statement ::=
 *   <dd> block_statement
 *   <br> | process_statement
 *   <br> | concurrent_procedure_call_statement
 *   <br> | concurrent_assertion_statement
 *   <br> | concurrent_signal_assignment_statement
 *   <br> | component_instantiation_statement
 *   <br> | generate_statement
 *   <br> | concurrent_break_statement
 */
class ScConcurrent_statement extends ScVhdl implements IScStatementBlock {
    IScStatementBlock item = null;
    public ScConcurrent_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCK_STATEMENT:
                item = new ScBlock_statement(c);
                break;
            case ASTPROCESS_STATEMENT:
                item = new ScProcess_statement(c);
                break;
            case ASTCONCURRENT_PROCEDURE_CALL_STATEMENT:
                item = new ScConcurrent_procedure_call_statement(c);
                break;
            case ASTCONCURRENT_ASSERTION_STATEMENT:
                item = new ScConcurrent_assertion_statement(c);
                break;
            case ASTCONCURRENT_SIGNAL_ASSIGNMENT_STATEMENT:
                item = new ScConcurrent_signal_assignment_statement(c);
                break;
            case ASTCOMPONENT_INSTANTIATION_STATEMENT:
                item = new ScComponent_instantiation_statement(c);
                break;
            case ASTGENERATE_STATEMENT:
                item = new ScGenerate_statement(c);
                break;
            case ASTCONCURRENT_BREAK_STATEMENT:
                item = new ScConcurrent_break_statement(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.toString();
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
    
    protected void setStatementPart(ScArchitecture_statement_part st) {
        if(item instanceof ScConcurrent_signal_assignment_statement) {
            ((ScConcurrent_signal_assignment_statement)item).setStatementPart(st);
        }
    }
}
