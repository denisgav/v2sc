package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> nature_definition ::=
 *   <dd> scalar_nature_definition
 *   <br> | composite_nature_definition
 */
class ScNature_definition extends ScVhdl {
    ScVhdl item = null;
    public ScNature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNATURE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSCALAR_NATURE_DEFINITION:
            item = new ScScalar_nature_definition(c);
            break;
        case ASTCOMPOSITE_NATURE_DEFINITION:
            item = new ScComposite_nature_definition(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}
