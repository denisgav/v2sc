package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  system_task_enable  <br>
 *     ::=  name_of_system_task  ; <br>
 *     ||=  name_of_system_task  (  expression  {, expression } ) ; 
 */
class ScSystem_task_enable extends ScVerilog {
    public ScSystem_task_enable(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSYSTEM_TASK_ENABLE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
