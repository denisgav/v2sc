package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> prefix ::=
 *   <dd> name
 *   <br> | function_call
 */
class ScPrefix extends ScVhdl {
    ScVhdl item = null;
    public ScPrefix(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPREFIX);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTNAME:
            item = new ScName(c);
            break;
        case ASTFUNCTION_CALL:
            item = new ScFunction_call(c);
            break;
        default:
            break;
        }
    }
    
    public String[] getNameSegments() {
        String[] segments = null;
        if(item instanceof ScName) {
            segments = ((ScName)item).getNameSegments();
        }else {
            segments = new String[1];
            segments[0] = item.scString();
        }
        return segments;
    }

    public String scString() {
        return item.scString();
    }
}
