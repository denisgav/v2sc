package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> record_type_definition ::=
 *   <dd> <b>record</b>
 *   <ul> element_declaration
 *   <br> { element_declaration }
 *   </ul><b>end</b> <b>record</b> [ <i>record_type_</i>simple_name ]
 */
class ScRecord_type_definition extends ScCommonIdentifier {
    ArrayList<ScElement_declaration> elements = new ArrayList<ScElement_declaration>();
    public ScRecord_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRECORD_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScElement_declaration ele = null;
            switch(c.getId())
            {
            case ASTELEMENT_DECLARATION:
                ele = new ScElement_declaration(c);
                elements.add(ele);
                break;
            default:
                break;
            }
        }
    }
    
    // operator ==
    String equalOp()
    {
        String ret = intent() + "inline bool operator == ";
        ret += "(const " + identifier + "& rhs) const" + System.lineSeparator();
        ret += startIntentBraceBlock();
        ret += intent() + "return (";
        for(int i = 0; i < elements.size(); i++) {
            ScElement_declaration decl = elements.get(i);
            for(int j = 0; j < decl.idList.items.size(); j++) {
                String iden = decl.idList.items.get(j).scString();
                ret += "(rhs." + iden + " == " + iden + ")";
                if(i < elements.size()-1 && j < decl.idList.items.size()-1) {
                    ret += " && ";
                }
            }
            if(i < elements.size()-1) {
                ret += " && ";
            }
        }
        ret += ");" + System.lineSeparator();
        ret += endIntentBraceBlock();
        return ret;
    }
    
    // operator =
    String assignmentOp()
    {
        String ret = intent() + "inline " + identifier + "& operator = ";
        ret += "(const " + identifier + "& rhs)" + System.lineSeparator();
        ret += startIntentBraceBlock();
        for(int i = 0; i < elements.size(); i++) {
            ScElement_declaration decl = elements.get(i);
            for(int j = 0; j < decl.idList.items.size(); j++) {
                String iden = decl.idList.items.get(j).scString();
                ret += addLFIntent(iden + " = " + "rhs." + iden + ";");
            }
        }
        ret += intent() + "return *this;" + System.lineSeparator();
        ret += endIntentBraceBlock();
        return ret;
    }
    
    // override sc_trace
    String traceMethod()
    {
        String ret = intent() + "inline friend void sc_trace(";
        ret += "sc_trace_file *tf";
        ret += ", const " + identifier + "& v";
        ret += ", const std::string& name";
        ret += ")" + System.lineSeparator();
        ret += startIntentBraceBlock();
        for(int i = 0; i < elements.size(); i++) {
            ScElement_declaration decl = elements.get(i);
            for(int j = 0; j < decl.idList.items.size(); j++) {
                String iden = decl.idList.items.get(j).scString();
                String strTmp = "sc_trace(tf, " + "v." + iden + ", "
                            + "name + \"." + iden + "\");";
                ret += addLFIntent(strTmp);
            }
        }
        ret += endIntentBraceBlock();
        return ret;
    }
    
    // operator <<
    String ostreamOp()
    {
        String ret = intent() + "inline friend ostream& operator << (";
        ret += "ostream& os";
        ret += ", const " + identifier + "& v";
        ret += ")" + System.lineSeparator();
        ret += startIntentBraceBlock();
        ret += intent() + "os << \"(\"";
        
        boolean boolSet = false;
        for(int i = 0; i < elements.size(); i++) {
            ScElement_declaration decl = elements.get(i);
            String type = decl.sub.scString();
            for(int j = 0; j < decl.idList.items.size(); j++) {
                String iden = decl.idList.items.get(j).scString();
                if(type.equals(scType[SC_BOOL]) && !boolSet) {
                    ret += " << std::boolalpha";
                    boolSet = true;
                }
                ret += " << " + "v." + iden;
            }
        }
        ret += " << \")\";" + System.lineSeparator();
        ret += intent() + "return os;" + System.lineSeparator();
        ret += endIntentBraceBlock();
        return ret;
    }

    public String scString() {
        String ret = "";
        ret += addLFIntent("struct " + identifier);

        ret += startIntentBraceBlock();
        for(int i = 0; i < elements.size(); i++) {
            ret += addLFIntent(elements.get(i).toString());
        }
        ret += System.lineSeparator();
        ret += addLF(equalOp());
        ret += addLF(assignmentOp());
        ret += addLF(traceMethod());
        ret += addLF(ostreamOp());
        endIntentBlock();
        ret += intent() + "}";
        return ret;
    }
}
