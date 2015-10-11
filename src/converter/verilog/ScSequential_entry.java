package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  sequential_entry  <br>
 *     ::=  input_list  :  state  :  next_state  ; 
 */
class ScSequential_entry extends ScVerilog {
    public ScSequential_entry(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSEQUENTIAL_ENTRY);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
