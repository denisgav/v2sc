package converter.vhdl;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


/**
 * <dl> selected_name ::=
 *   <dd> prefix . suffix
 */
class ScSelected_name extends ScVhdl {
    ScPrefix prefix = null;
    ScSuffix suffix = null;
    public ScSelected_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSELECTED_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new ScPrefix(c);
                break;
            case ASTSUFFIX:
                suffix = new ScSuffix(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        String[] segs = getNameSegments();
        Symbol sym = (Symbol)parser.getSymbol(curNode, segs);
        if(sym == null || sym.typeRange == null) {
            return 1;
        }
        return getWidth(sym.typeRange[0], sym.typeRange[2]);
    }
    
    public String[] getNameSegments() {
        String[] psegs = prefix.getNameSegments();
        String[] segments = new String[psegs.length + 1];
        System.arraycopy(psegs, 0, segments, 0, psegs.length);
        segments[psegs.length] = suffix.scString();
        return segments;
    }

    public String scString() {
        String ret = "";
        ret += prefix.scString();
        ret += ".";
        ret += suffix.scString();
        return ret;
    }
}
