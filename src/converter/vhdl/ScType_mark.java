package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> type_mark ::=
 *   <dd> <i>type_</i>name
 *   <br> | <i>subtype_</i>name
 */
class ScType_mark extends ScVhdl {
    ScName name = null;
    public ScType_mark(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_MARK);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String getTypeString(String[] range) {
        return getReplaceType(name.scString(), range);
    }

    public String scString() {
        return getReplaceType(name.scString(), null);
    }
}
