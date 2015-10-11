package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  state  <br>
 *     ::=  level_symbol  
 */
class ScState extends ScVerilog {
    public ScState(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTATE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
