package converter.vhdl;

import java.util.ArrayList;

import converter.IScStatementBlock;
import parser.Token;
import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


/**
 * <dl> architecture_statement_part ::=
 *   <dd> { architecture_statement }
 */
class ScArchitecture_statement_part extends ScVhdl implements IScStatementBlock {
    ArrayList<ScArchitecture_statement> items = new ArrayList<ScArchitecture_statement>();
    
    ArrayList<ScVhdl> signalAssignments = new ArrayList<ScVhdl>();
    String assProcessName = "";

    public ScArchitecture_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARCHITECTURE_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScArchitecture_statement st = new ScArchitecture_statement(c);
            st.setStatementPart(this);
            items.add(st);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += intent() + items.get(i).scString();
            if(i < items.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }

    @Override
    public String getDeclaration() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += addLF(items.get(i).getDeclaration());
        }
        
        if(signalAssignments.size() > 0) {
            ret += addLF(intent() + "void " + assProcessName + "();");
        }
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += addLF(items.get(i).getImplements());
        }
        
        if(signalAssignments.size() > 0) {
            ret += intent() + "void " + assProcessName + "()" + System.lineSeparator();
            ret += startIntentBraceBlock();
            for(int i = 0; i < signalAssignments.size(); i++) {
                ret += addLF(signalAssignments.get(i).toString());
            }
            ret += endIntentBraceBlock();
        }
        
        return ret;
    }

    @Override
    public String getInitCode()
    {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += addLF(items.get(i).getInitCode());
        }
        
        if(signalAssignments.size() > 0) {
            ArrayList<String> senList = getAssignmentSensitiveList();
            if(senList.size() > 0) {
                ret += intent() + "SC_METHOD(" + assProcessName + ");" + System.lineSeparator();
                ret += intent() + "sensitive";
                for(int i = 0; i < senList.size(); i++) {
                    ret += " << " + senList.get(i);
                }
                ret += ";" + System.lineSeparator();
            } else {
                // no sensitive signal(may assign constant value)
                ret += intent() + assProcessName + "();" + System.lineSeparator();
            }
        }
        
        return ret;
    }
    
    private ArrayList<String> getAssignmentSensitiveList() {
        ArrayList<String> senList = new ArrayList<String>();
        for(int i = 0; i < signalAssignments.size(); i++) {
            ScConcurrent_signal_assignment_statement ass = 
                (ScConcurrent_signal_assignment_statement)signalAssignments.get(i);
            ScVhdl sv = null;
            if(ass.signal_assignment instanceof ScConditional_signal_assignment) {
                sv = ((ScConditional_signal_assignment)ass.signal_assignment).waveforms;

            }else {
                // ScSelected_signal_assignment
                sv = ((ScSelected_signal_assignment)ass.signal_assignment).selected_waveforms;
            }
            
            Token tkn = sv.curNode.getFirstToken();
            Symbol sym;
            while(tkn != sv.curNode.getLastToken()) {
                sym = (Symbol)parser.getSymbol(curNode, tkn.image);
                if(sym != null && (sym.kind == SIGNAL || sym.kind == PORT)) {
                    int k = 0;
                    for(k = 0; k < senList.size(); k++) {
                        if(tkn.image.equalsIgnoreCase(senList.get(k))) {
                            break;
                        }
                    }
                    if(k >= senList.size())
                        senList.add(tkn.image);
                }
                tkn = tkn.next;
            }
            
            sym = (Symbol)parser.getSymbol(curNode, tkn.image);
            if(sym != null && (sym.kind == SIGNAL || sym.kind == PORT)) {
                int k = 0;
                for(k = 0; k < senList.size(); k++) {
                    if(tkn.image.equalsIgnoreCase(senList.get(k))) {
                        break;
                    }
                }
                if(k >= senList.size())
                    senList.add(tkn.image);
            }
        }
        return senList;
    }
    
    protected void addSignalAssignment(ScVhdl ass) {
        if(assProcessName.isEmpty()) {
            assProcessName = addMethodName("process_comp_assignment");
        }
        signalAssignments.add(ass);
    }
}
