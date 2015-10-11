package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> use_clause ::=
 *   <dd> <b>use</b> selected_name { , selected_name } ;
 */
class ScUse_clause extends ScVhdl {
    static final String IEEE = "ieee";
    static final String STD = "std";
    static final String WORK = "work";
    
    ArrayList<ScSelected_name> names = new ArrayList<ScSelected_name>();
    
    public ScUse_clause(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTUSE_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScSelected_name newNode = null;
            switch(c.getId())
            {
            case ASTSELECTED_NAME:
                newNode = new ScSelected_name(c);
                names.add(newNode);
                break;
            default:
                break;
            }
        }
    }
    
    public String[] getPackageNames() {
        ArrayList<String> ret = new ArrayList<String>();
        for(int i = 0; i < names.size(); i++) {
            String[] segs = names.get(i).getNameSegments();
            if(segs[0].equalsIgnoreCase(IEEE) || segs[0].equalsIgnoreCase(STD)) {
                //TODO do something
                continue;
            }
            
            ret.add(segs[segs.length - 2]);
        }
        return (String[])ret.toArray(new String[ret.size()]);
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < names.size(); i++) {
            String[] segs = names.get(i).getNameSegments();
            if(segs[0].equalsIgnoreCase(IEEE) || segs[0].equalsIgnoreCase(STD)) {
                //TODO do something
                continue;
            }
            
            ret += "#include \"";
            ret += segs[segs.length - 2];
            ret += ".h\"";
        }
        return ret;
    }
}
