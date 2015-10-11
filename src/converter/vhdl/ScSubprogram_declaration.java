package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> subprogram_declaration ::=
 *   <dd> subprogram_specification ;
 */
class ScSubprogram_declaration extends ScVhdl {
    ScVhdl spec = null;
    public ScSubprogram_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSUBPROGRAM_DECLARATION);
        spec = new ScSubprogram_specification((ASTNode)node.getChild(0));
    }

    public String scString() {
        return spec.scString() + ";";
    }
}
