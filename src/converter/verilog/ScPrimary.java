package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  primary  <br>
 *     ::=  number  <br>
 *     ||=  identifier  <br>
 *     ||=  identifier  <b>[</b> <expression> <b>]</b> <br>
 *     ||=  identifier  <b>[</b> <constant_expression> : <constant_expression> <b>]</b> <br>
 *     ||=  concatenation  <br>
 *     ||=  multiple_concatenation  <br>
 *     ||=  function_call  <br>
 *     ||= (  mintypmax_expression  ) 
 */
class ScPrimary extends ScVerilog {
    public ScPrimary(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPRIMARY);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
