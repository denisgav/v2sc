package converter.vhdl;

import parser.vhdl.ASTNode;
import java.util.ArrayList;


/**
 * <dl> wait_statement ::=
 *   <dd> [ label : ] <b>wait</b> [ sensitivity_clause ] 
 *                [ condition_clause ] [ timeout_clause ] ;
 */
class ScWait_statement extends ScVhdl {
    ScSensitivity_clause sensitivity = null;
    ScCondition_clause condition = null;
    ScTimeout_clause timeout = null;
    public ScWait_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTWAIT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSENSITIVITY_CLAUSE:
                sensitivity = new ScSensitivity_clause(c);
                break;
            case ASTCONDITION_CLAUSE:
                condition = new ScCondition_clause(c);
                break;
            case ASTTIMEOUT_CLAUSE:
                timeout = new ScTimeout_clause(c);
                break;
            default:
                break;
            }
        }
    }
    
    private String getWaitString() {
        String ret = intent();
        if(timeout != null) {
            ret += "wait(";
            ret += timeout.scString();
            ret += ", ";
            ret += getSCTime(timeout.getTimeUnitName());
        }else if(sensitivity != null) {
            ret += "wait(";
        }else {
            ret += "next_trigger(";
        }
        
        if(sensitivity != null) {
            ArrayList<String> sensList = sensitivity.getSensitiveList();
            for(int i = 0; i < sensList.size(); i++) {
                ret += sensList.get(i) + ".event()";
                if(i < sensList.size() - 1) {
                    ret += " | ";
                }
            }
        }
        ret += ");";
        return ret;
    }

    public String scString() {
        String ret = "";
        if(condition != null) {
            ret += intent() + "while(!(";
            ret += condition.scString();
            ret += ")){" + System.lineSeparator();
            startIntentBlock();
            ret += getWaitString();
            endIntentBlock();
            ret += System.lineSeparator() + intent() + "}";
        }else {
            ret += getWaitString();
        }
        return ret;
    }
}
