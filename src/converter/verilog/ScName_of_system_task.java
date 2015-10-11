package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_system_task  <br>
 *     ::= $ system_identifier 
 *     (Note: the $ may <b>not</b> be followed by a space.) 
 */
class ScName_of_system_task extends ScVerilog {
    public ScName_of_system_task(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_SYSTEM_TASK);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
