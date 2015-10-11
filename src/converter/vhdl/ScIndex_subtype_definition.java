package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> index_subtype_definition ::=
 *   <dd> type_mark <b>range</b> <>
 */
class ScIndex_subtype_definition extends ScVhdl {
    ScType_mark type_mark = null;
    public ScIndex_subtype_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEX_SUBTYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return type_mark.scString();
    }
}
