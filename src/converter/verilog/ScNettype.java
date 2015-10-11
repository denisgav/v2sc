package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  nettype  is one of the following keywords: <br>
 *     <b>wire</b> <b>tri</b> <b>tri1</b> <b>supply0</b> 
 *     <b>wand</b> <b>triand</b> <b>tri0</b> <b>supply1</b>
 *     <b>wor</b> <b>trior</b> <b>trireg</b> 
 */
class ScNettype extends ScVerilog {
    String image = "";
    public ScNettype(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNETTYPE);
        image = node.firstTokenImage();
    }

    public String scString() {
        return image;
    }
}
