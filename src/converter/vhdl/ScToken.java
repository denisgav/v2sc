package converter.vhdl;

import parser.vhdl.ASTNode;


class ScToken extends ScVhdl {
    String image = null;
    public ScToken(ASTNode node) {
        super(node);
        assert(node.getId() == ASTVOID);
        image = node.firstTokenImage();
    }

    public String scString() {
        return image;
    }
}
