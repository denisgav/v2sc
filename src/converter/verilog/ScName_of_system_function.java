package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_system_function  <br>
 *     ::= $ system_identifier  <br>
 *     (Note: the $ may <b>not</b> be followed by a space.) 
 */
class ScName_of_system_function extends ScVerilog {
    public ScName_of_system_function(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_SYSTEM_FUNCTION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
