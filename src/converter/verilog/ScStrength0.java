package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  strength0  is one of the following keywords: <br>
 *     <b>supply0</b> <b>strong0</b> <b>pull0</b> <b>weak0</b> <b>highz0</b> 
 */
class ScStrength0 extends ScVerilog {
    String image = "";
    public ScStrength0(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTRENGTH0);
        image = node.firstTokenImage();
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
