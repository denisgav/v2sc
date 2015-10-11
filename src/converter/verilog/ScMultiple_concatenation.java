package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  multiple_concatenation  <br>
 *     ::= <b>{</b>  expression  <b>{</b>  expression  {, expression } <b>}</b> <b>}</b> 
 */
class ScMultiple_concatenation extends ScVerilog {
    public ScMultiple_concatenation(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMULTIPLE_CONCATENATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
