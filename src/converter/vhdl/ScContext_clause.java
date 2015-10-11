package converter.vhdl;

import java.util.ArrayList;

import converter.IncludePath;
import parser.vhdl.ASTNode;


/**
 * <dl> context_clause ::=
 *   <dd> { context_item }
 */
class ScContext_clause extends ScVhdl {
    ArrayList<ScContext_item> items = new ArrayList<ScContext_item>();
    public ScContext_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONTEXT_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScContext_item item = new ScContext_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            String tmp = items.get(i).toString();
            ret += tmp;
            if(i < items.size() - 1 && !tmp.isEmpty()) {
                ret += System.lineSeparator();
            }
        }
        
        ret += System.lineSeparator();
        for(int i = 0; i < items.size(); i++) {
            ScContext_item item = items.get(i);
            if(item.isUse) {
                ScUse_clause useClause = (ScUse_clause)item.item;
                String[] pkgs = useClause.getPackageNames();
                for(int j = 0; j < pkgs.length; j++) {
                    ret += "using namespace " + pkgs[j];
                    ret += ";" + System.lineSeparator();
                }
            }
        }
        return ret;
    }
    
    public IncludePath[] getInclude()
    {
        String tmp = "";
        ArrayList<IncludePath> ret = new ArrayList<IncludePath>();
        for(int i = 0; i < items.size(); i++) {
            tmp = items.get(i).scString();
            if(!tmp.isEmpty()) {
                ret.add(new IncludePath(tmp));
            }
        }
        for(int i = 0; i < items.size(); i++) {
            ScContext_item item = items.get(i);
            if(item.isUse) {
                ScUse_clause useClause = (ScUse_clause)item.item;
                String[] pkgs = useClause.getPackageNames();
                for(int j = 0; j < pkgs.length; j++) {
                    tmp = "using namespace " + pkgs[j] + ";";
                    ret.add(new IncludePath(tmp));
                }
            }
        }
        return ret.toArray(new IncludePath[ret.size()]);
    }
}
