package converter.vhdl;

import parser.vhdl.ASTNode;
import java.util.StringTokenizer;
import parser.vhdl.Symbol;
import parser.vhdl.VhdlTokenManager;


/**
 * <dl> variable_declaration ::=
 *   <dd> [ <b>shared</b> ] <b>variable</b> identifier_list : subtype_indication [ := expression ] ;
 */
class ScVariable_declaration extends ScCommonDeclaration {
    public ScVariable_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTVARIABLE_DECLARATION);
    }
    
    protected boolean isTypeValid() {
        boolean ret = true;
        StringTokenizer tkn = new StringTokenizer(sub.scString(), VhdlTokenManager.specialChar);
        while(tkn.hasMoreTokens()) {
            String tmp = tkn.nextToken().trim();
            Symbol sym = (Symbol)parser.getSymbol(curNode, tmp);
            if(sym != null && sym.kind == VARIABLE) {
                ret = false;
                break;
            }
        }
        return ret;
    }
    
    public String scString() {
        String ret = intent() + super.scString();
        ret += ";";
        return ret;
    }
}
