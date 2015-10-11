package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  function_call  <br>
 *     ::=  name_of_function  (  expression  {, expression } ) <br>
 *     ||=  name_of_system_function  (  expression  {, expression } ) <br>
 *     ||=  name_of_system_function  
 */
class ScFunction_call extends ScVerilog {
    public ScFunction_call(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFUNCTION_CALL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
