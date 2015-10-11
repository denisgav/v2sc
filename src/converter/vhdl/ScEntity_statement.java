package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_statement ::=
 *   <dd> concurrent_assertion_statement
 *   <br> | <i>passive_</i>concurrent_procedure_call_statement
 *   <br> | <i>passive_</i>process_statement
 */
class ScEntity_statement extends ScVhdl {
    ScVhdl item = null;
    public ScEntity_statement(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTENTITY_STATEMENT);
        switch(node.getId())
        {
        case ASTCONCURRENT_ASSERTION_STATEMENT:
            item = new ScConcurrent_assertion_statement(node);
            break;
        case ASTCONCURRENT_PROCEDURE_CALL_STATEMENT:
            item = new ScConcurrent_procedure_call_statement(node);
            break;
        case ASTPROCESS_STATEMENT:
            item = new ScProcess_statement(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}
