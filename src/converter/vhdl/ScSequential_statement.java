package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> sequential_statement ::=
 *   <dd> wait_statement
 *   <br> | assertion_statement
 *   <br> | report_statement
 *   <br> | signal_assignment_statement
 *   <br> | variable_assignment_statement
 *   <br> | procedure_call_statement
 *   <br> | if_statement
 *   <br> | case_statement
 *   <br> | loop_statement
 *   <br> | next_statement
 *   <br> | exit_statement
 *   <br> | return_statement
 *   <br> | null_statement
 *   <br> | break_statement
 */
class ScSequential_statement extends ScVhdl {
    ScVhdl item = null;
    public ScSequential_statement(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSEQUENTIAL_STATEMENT);
        switch(node.getId())
        {
        case ASTWAIT_STATEMENT:
            item = new ScWait_statement(node);
            break;
        case ASTASSERTION_STATEMENT:
            item = new ScAssertion_statement(node);
            break;
        case ASTREPORT_STATEMENT:
            item = new ScReport_statement(node);
            break;
        case ASTSIGNAL_ASSIGNMENT_STATEMENT:
            item = new ScSignal_assignment_statement(node);
            break;
        case ASTVARIABLE_ASSIGNMENT_STATEMENT:
            item = new ScVariable_assignment_statement(node);
            break;
        case ASTPROCEDURE_CALL_STATEMENT:
            item = new ScProcedure_call_statement(node);
            break;
        case ASTIF_STATEMENT:
            item = new ScIf_statement(node);
            break;
        case ASTCASE_STATEMENT:
            item = new ScCase_statement(node);
            break;
        case ASTLOOP_STATEMENT:
            item = new ScLoop_statement(node);
            break;
        case ASTNEXT_STATEMENT:
            item = new ScNext_statement(node);
            break;
        case ASTEXIT_STATEMENT:
            item = new ScExit_statement(node);
            break;
        case ASTRETURN_STATEMENT:
            item = new ScReturn_statement(node);
            break;
        case ASTNULL_STATEMENT:
            item = new ScNull_statement(node);
            break;
        case ASTBREAK_STATEMENT:
            item = new ScBreak_statement(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.toString();
    }
}
