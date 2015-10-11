package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  task_enable  <br>
 *     ::=  name_of_task  ; <br>
 *     ||=  name_of_task  (  expression  {, expression } ) ; 
 */
class ScTask_enable extends ScVerilog {
    public ScTask_enable(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTASK_ENABLE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
