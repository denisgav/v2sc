package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> concurrent_signal_assignment_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] conditional_signal_assignment
 *   <br> | [ label : ] [ <b>postponed</b> ] selected_signal_assignment
 */
class ScConcurrent_signal_assignment_statement extends ScCommonIdentifier implements IScStatementBlock {
    ScVhdl signal_assignment = null;
    public ScConcurrent_signal_assignment_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_SIGNAL_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCONDITIONAL_SIGNAL_ASSIGNMENT:
                signal_assignment = new ScConditional_signal_assignment(c);
                break;
            case ASTSELECTED_SIGNAL_ASSIGNMENT:
                signal_assignment = new ScSelected_signal_assignment(c);
                break;
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                break;
            default:
                break;
            }
        }
        if(identifier.isEmpty())
            identifier = String.format("line%d", node.getFirstToken().beginLine);
    }

    public String scString() {
        return signal_assignment.scString();
    }

    @Override
    public String getDeclaration() {
        return "";
    }

    @Override
    public String getImplements() {
        return "";
    }

    @Override
    public String getInitCode()
    {
        return "";
    }
    
    protected void setStatementPart(ScArchitecture_statement_part st) {
        st.addSignalAssignment(this);
    }
}
