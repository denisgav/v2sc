package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> composite_nature_definition ::=
 *   <dd> array_nature_definition
 *   <br> | record_nature_definition
 */
class ScComposite_nature_definition extends ScVhdl {
    ScVhdl item = null;
    public ScComposite_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPOSITE_NATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTARRAY_NATURE_DEFINITION:
                item = new ScArray_nature_definition(c);
                break;
            case ASTRECORD_NATURE_DEFINITION:
                item = new ScRecord_nature_definition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }
}
