package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  delay_control  <br>
 *     ::= #  number  <br>
 *     ||= #  identifier  <br>
 *     ||= # (  mintypmax_expression  ) 
 */
class ScDelay_control extends ScVerilog {
    public ScDelay_control(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDELAY_CONTROL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
