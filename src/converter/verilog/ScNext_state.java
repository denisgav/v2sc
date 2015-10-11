package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  next_state  <br>
 *     ::=  output_symbol  <br>
 *     ||= - (This is a literal hyphen, see Chapter 5 <b>for</b> details). 
 */
class ScNext_state extends ScVerilog {
    public ScNext_state(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNEXT_STATE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
