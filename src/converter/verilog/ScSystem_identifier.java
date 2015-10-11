package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  system_identifier  <br>
 *     An  IDENTIFIER  assigned to an existing system <b>task</b> <b>or</b> <b>function</b> 
 */
class ScSystem_identifier extends ScVerilog {
    public ScSystem_identifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSYSTEM_IDENTIFIER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
