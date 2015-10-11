package converter.vhdl;

import java.util.ArrayList;

import converter.IScStatementBlock;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_case_statement ::=
 *   <dd> [ <i>case_</i>label : ]
 *   <ul> <b>case</b> expression <b>use</b>
 *   <ul> simultaneous_alternative
 *   <br> { simultaneous_alternative }
 *   </ul> <b>end</b> <b>case</b> [ <i>case_</i>label ] ; </ul>
 */
class ScSimultaneous_case_statement extends ScCommonIdentifier implements IScStatementBlock {
    ScVhdl expression = null;
    ArrayList<ScVhdl> alts = new ArrayList<ScVhdl>();
    public ScSimultaneous_case_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSIMULTANEOUS_CASE_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            case ASTSIMULTANEOUS_ALTERNATIVE:
                newNode = new ScSimultaneous_alternative(c);
                alts.add(newNode); 
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
    
    private boolean hasRange() {
        boolean ret = false;
        for(int i = 0; i < alts.size(); i++) {
            ScSimultaneous_alternative alt = (ScSimultaneous_alternative)alts.get(i);
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
            for(int i = 0; i < alts.size(); i++) {
                ScSimultaneous_alternative alt = (ScSimultaneous_alternative)alts.get(i);
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
                    ret += intent() + "if(" + tmp + ")";
                }else {
                    ret += intent() + "else if(" + tmp + ")" + System.lineSeparator();
                }

                ret += startIntentBraceBlock();
                ret += addLFIntent(alt.statement_part.toString());
                ret += endIntentBraceBlock();
            }
        }else {
            ret += intent() + "switch(" + expression.scString() + ")" + System.lineSeparator();
            ret += intent() + "{" + System.lineSeparator();
            for(int i = 0; i < alts.size(); i++) {
                ret += addLF(alts.get(i).toString());
            }
            ret += intent() + "}" + System.lineSeparator();
        }

        return ret;
    }

    @Override
    public String getDeclaration()
    {
        return "";
    }

    @Override
    public String getImplements()
    {
        return "";
    }

    @Override
    public String getInitCode()
    {
        return toString();
    }
}
