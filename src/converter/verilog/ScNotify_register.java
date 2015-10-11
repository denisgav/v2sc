package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  notify_register  <br>
 *     ::=  identifier  
 */
class ScNotify_register extends ScVerilog {
    public ScNotify_register(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNOTIFY_REGISTER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
