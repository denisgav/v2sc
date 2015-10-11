package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> case_statement ::=
 *   <dd> [ <i>case_</i>label : ]
 *   <ul> <b>case</b> expression <b>is</b>
 *   <ul> case_statement_alternative
 *   <br> { case_statement_alternative }
 *   </ul> <b>end</b> <b>case</b> [ <i>case_</i>label ] ; </ul>
 */
class ScCase_statement extends ScVhdl {
	ScExpression expression = null;
    ArrayList<ScCase_statement_alternative> statement_alt = 
    			new ArrayList<ScCase_statement_alternative>();
    public ScCase_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCASE_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScCase_statement_alternative newNode = null;
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            case ASTCASE_STATEMENT_ALTERNATIVE:
                newNode = new ScCase_statement_alternative(c);
                statement_alt.add(newNode); 
                break;
            default:
                break;
            }
        }
    }
    
    private boolean hasRange() {
        boolean ret = false;
        for(int i = 0; i < statement_alt.size(); i++) {
            ScCase_statement_alternative alt = statement_alt.get(i);
            ScChoices choices = alt.getChoices();
            if(choices.hasRange()) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public String scString() {
        String ret = "";
        String val = expression.scString();
        if(hasRange()) {
            for(int i = 0; i < statement_alt.size(); i++) {
                ScCase_statement_alternative alt = statement_alt.get(i);
                ScChoices choices = alt.getChoices();
                ArrayList<ScChoice> items = choices.getItems();
                String tmp = "";
                boolean isElse = false;
                for(int j = 0; j < items.size(); j++) {
                    ScChoice choice = items.get(j);
                    if(choice.isRange()) {
                        ScDiscrete_range range = (ScDiscrete_range)choice.item;
                        tmp += "(" + val + " >= " + range.getMin() + " && ";
                        tmp += val + " <= " + range.getMax() + ")";
                    }else if(choice.isOthers()) {
                        isElse = true;
                        break;
                    }else {
                        tmp += val + " == " + choice.scString();
                    }
                    
                    if(j < items.size() - 1) {
                        tmp += " || ";
                    }
                }
                
                if(isElse) {
                    ret += intent() + "else" + System.lineSeparator();
                }else if(i == 0) {
                    ret += intent() + "if(" + tmp + ")" + System.lineSeparator();
                }else {
                    ret += intent() + "else if(" + tmp + ")" + System.lineSeparator();
                }
                ret += startIntentBraceBlock();
                ret += addLFIntent(alt.seq_statements.toString());
                ret += endIntentBraceBlock();
            }
        }else {
            ret += intent() + "switch(" + expression.scString() + ")" + System.lineSeparator();
            ret += intent() + "{" + System.lineSeparator();
            for(int i = 0; i < statement_alt.size(); i++) {
                ret += addLF(statement_alt.get(i).toString());
            }
            ret += intent() + "}" + System.lineSeparator();
        }

        return ret;
    }
}
