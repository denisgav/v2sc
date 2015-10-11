package converter.verilog;

import parser.verilog.ASTNode;

/**
 */
class ScVerilog_token extends ScVerilog {
    public ScVerilog_token(ASTNode node) {
        super(node);
        assert(node.getId() == ASTVERILOG_TOKEN);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
