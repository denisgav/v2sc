package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  statement  <br>
 *     ::= blocking_assignment  ; <br>
 *     ||=  non_blocking_assignment  ; <br>
 *     ||= <b>if</b> (  expression  )  statement_or_null  <br>
 *     ||= <b>if</b> (  expression  )  statement_or_null  <b>else</b>  statement_or_null  <br>
 *     ||= <b>case</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
 *     ||= <b>casez</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
 *     ||= <b>casex</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
 *     ||= <b>forever</b>  statement  <br>
 *     ||= <b>repeat</b> (  expression  )  statement  <br>
 *     ||= <b>while</b> (  expression  )  statement  <br>
 *     ||= <b>for</b> (  assignment  ;  expression  ;  assignment  )  statement  <br>
 *     ||=  delay_or_event_control   statement_or_null  <br>
 *     ||= <b>wait</b> (  expression  )  statement_or_null  <br>
 *     ||= ->  name_of_event  ; <br>
 *     ||=  seq_block  <br>
 *     ||=  par_block  <br>
 *     ||=  task_enable  <br>
 *     ||=  system_task_enable  <br>
 *     ||= <b>disable</b>  name_of_task  ; <br>
 *     ||= <b>disable</b>  name_of_block  ; <br>
 *     ||= <b>assign</b>  assignment  ; <br>
 *     ||= <b>deassign</b>  lvalue  ; <br>
 *     ||= <b>force</b>  assignment  ; <br>
 *     ||= <b>release</b>  lvalue  ; 
 */
class ScStatement extends ScVerilog {
    ScVerilog statement = null;
    public ScStatement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {

            case ASTIF_STATEMENT:
                statement = new ScIf_statement(c);
                break;
            case ASTCASE_STATEMENT:
                statement = new ScCase_statement(c);
                break;
            case ASTLOOP_STATEMENT:
                statement = new ScLoop_statement(c);
                break;
            case ASTDELAY_OR_EVENT_STATEMENT:
                statement = new ScDelay_or_event_statement(c);
                break;
            case ASTWAIT_STATEMENT:
                statement = new ScWait_statement(c);
                break;
            case ASTEVENT_TRIGGER_STATEMENT:
                statement = new ScEvent_trigger_statement(c);
                break;
            case ASTSEQ_BLOCK:
                statement = new ScSeq_block(c);
                break;
            case ASTPAR_BLOCK:
                statement = new ScPar_block(c);
                break;
            case ASTDISABLE_TASK_OR_BLOCK:
                statement = new ScDisable_task_or_block(c);
                break;
            case ASTASSIGN_ASSIGNMENT:
                statement = new ScAssign_assignment(c);
                break;
            case ASTDEASSIGN_LVALUE:
                statement = new ScDeassign_lvalue(c);
                break;
            case ASTFORCE_ASSIGNMENT:
                statement = new ScForce_statement(c);
                break;
            case ASTRELEASE_LVALUE:
                statement = new ScRelease_statement(c);
                break;
            case ASTNON_BLOCK_ASSIGNMENT_STATEMENT:
                statement = new ScNon_block_assignment_statement(c);
                break;
            case ASTBLOCK_ASSIGNMENT_STATEMENT:
                statement = new ScBlock_assignment_statement(c);
                break;
            case ASTSYSTEM_TASK_ENABLE:
                statement = new ScSystem_task_enable(c);
                break;
            case ASTTASK_ENABLE:
                statement = new ScTask_enable(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return statement.toString();
    }
}
