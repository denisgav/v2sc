package converter.vhdl;

import java.util.ArrayList;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_alternative ::=
 *   <dd> <b>when</b> choices =>
 *   <ul> simultaneous_statement_part </ul>
 */
class ScSimultaneous_alternative extends ScVhdl {
    ScChoices choices = null;
    ScVhdl statement_part = null;
    public ScSimultaneous_alternative(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_ALTERNATIVE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCHOICES:
                choices = new ScChoices(c);
                break;
            case ASTSIMULTANEOUS_STATEMENT_PART:
                statement_part = new ScSimultaneous_statement_part(c);
                break;
            default:
                break;
            }
        }
    }

    public ScChoices getChoices() {
        return choices;
    }
    
    public String statementsString() {
        return statement_part.scString();
    }

    public String scString() {
        String ret = "";
        ArrayList<ScChoice> items = choices.getItems();
        for(int i = 0; i < items.size(); i++) {
            ScChoice item = items.get(i);
            if(item.isOthers()){
                ret += intent() + "default:" + System.lineSeparator();
            }else {
                ret += intent() + "case " + item.scString();
                ret += ":" + System.lineSeparator();
            }
        }
        startIntentBlock();
        ret += statementsString();
        ret += intent() + "break;" + System.lineSeparator();
        endIntentBlock();
        return ret;
    }
}
