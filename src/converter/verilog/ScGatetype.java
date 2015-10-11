package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  gatetype  is one of the following keywords: <br>
 *     <b>and</b> <b>nand</b> <b>or</b> <b>nor</b> <b>xor</b> <b>xnor</b> <b>buf</b> <b>bufif0</b> <b>bufif1</b> <b>not</b> <b>notif0</b> <b>notif1</b> <b>pulldown</b> <b>pullup</b> <br>
 *     <b>nmos</b> <b>rnmos</b> <b>pmos</b> <b>rpmos</b> <b>cmos</b> <b>rcmos</b> <b>tran</b> <b>rtran</b> <b>tranif0</b> <b>rtranif0</b> <b>tranif1</b> <b>rtranif1</b> 
 */
class ScGatetype extends ScVerilog {
    String image = "";
    public ScGatetype(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGATETYPE);
        image = node.firstTokenImage();
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
