package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> if_statement ::=
 *   <dd> [ <i>if_</i>label : ]
 *   <ul> <b>if</b> condition <b>then</b>
 *   <ul>  sequence_of_statements
 *   </ul> { <b>elsif</b> condition <b>then</b>
 *   <ul> sequence_of_statements }
 *   </ul> [ <b>else</b>
 *   <ul> sequence_of_statements ]
 *   </ul> <b>end</b> <b>if</b> [ <i>if_</i>label ] ; </ul>
 */
class ScIf_statement extends ScVhdl {
    class ConPair {
        ScCondition condition = null;
        ScSequence_of_statements seq_statements = null;
    }
    ConPair if_pair = new ConPair();
    ArrayList<ConPair> elsif_pair = new ArrayList<ConPair>();
    ConPair else_pair = null;
    public ScIf_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTIF_STATEMENT);
        int i = 0;
        while(i < node.getChildrenNum()) {
            ASTNode c = (ASTNode)node.getChild(i);
            String image = "";
            switch(c.getId())
            {
            case ASTVOID:
                image = c.firstTokenImage();
                c = (ASTNode)node.getChild(i+1);
                if(image.equalsIgnoreCase(tokenImage[IF])) {
                    if_pair.condition = new ScCondition(c);
                    c = (ASTNode)node.getChild(i+2);
                    if_pair.seq_statements = new ScSequence_of_statements(c);
                    i += 3;
                }else if(image.equalsIgnoreCase(tokenImage[ELSIF])) {
                    ConPair pair = new ConPair();
                    pair.condition = new ScCondition(c);
                    c = (ASTNode)node.getChild(i+2);
                    pair.seq_statements = new ScSequence_of_statements(c);
                    elsif_pair.add(pair);
                    i += 3;
                }else if(image.equalsIgnoreCase(tokenImage[ELSE])) {
                    else_pair = new ConPair();
                    else_pair.seq_statements = new ScSequence_of_statements(c);
                    i += 2;
                }
                break;

            default:
                i ++;
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += intent() + "if(" + if_pair.condition.scString() + ")" + System.lineSeparator();

        ret += startIntentBraceBlock();
        ret += if_pair.seq_statements.scString();
        ret += endIntentBraceBlock();

        if(elsif_pair.size() > 0) {
            for(int i = 0; i < elsif_pair.size(); i++) {
                ConPair pair = elsif_pair.get(i);
                ret += intent() + "else if(" + pair.condition.scString() + ")" + System.lineSeparator();

                ret += startIntentBraceBlock();
                ret += pair.seq_statements.scString();
                ret += endIntentBraceBlock();
            }
        }
        
        if(else_pair != null) {
            ret += intent() + "else" + System.lineSeparator();
            ret += startIntentBraceBlock();
            ret += else_pair.seq_statements.scString();
            ret += endIntentBraceBlock();
        }
        return ret;
    }
}
