package converter.verilog;

import parser.verilog.ASTNode;

/**
 *   <b>forever</b>  statement  <br>
 *   <b>repeat</b> (  expression  )  statement  <br>
 *   <b>while</b> (  expression  )  statement  <br>
 *   <b>for</b> (  assignment  ;  expression  ;  assignment  )  statement
 */
class ScLoop_statement extends ScVerilog {
    ScExpression expression = null;
    ScStatement statement = null;
    ScAssignment assign1 = null, assign2 = null;
    /** 0-forever, 1-repeat, 2-while, 3-for */
    int type = 0;
    public ScLoop_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTLOOP_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            case ASTSTATEMENT:
                statement = new ScStatement(c);
                break;
            case ASTASSIGNMENT:
                if(assign1 == null) {
                    assign1 = new ScAssignment(c);
                }else {
                    assign2 = new ScAssignment(c);
                }
                break;
            case ASTVERILOG_TOKEN:
                if(c.firstTokenImage().equals("forever")) {
                    type = 0;
                }else if(c.firstTokenImage().equals("repeat")) {
                    type = 1;
                }else if(c.firstTokenImage().equals("while")) {
                    type = 2;
                }else if(c.firstTokenImage().equals("for")) {
                    type = 3;
                }
                break;
            default:
                break;
            }
        }
    }
    
    public String scString() {
        String ret = "";
        switch(type)
        {
        case 0:
            ret += intent() + "while(1)" + System.lineSeparator();
            ret += startIntentBraceBlock();
            ret += statement.toString();
            ret += endIntentBraceBlock();
            break;
        case 1:
            ret += intent() + "i = 0" + expression.toString();
            ret += intent() + "while(i > 0)" + System.lineSeparator();
            ret += startIntentBraceBlock();
            ret += statement.toString();
            ret += intent() + "i--;" + System.lineSeparator();
            ret += endIntentBraceBlock();
            break;
        case 2:
            ret += intent() + "while(";
            ret += expression.toString() + ")" + System.lineSeparator();
            ret += startIntentBraceBlock();
            ret += statement.toString();
            ret += endIntentBraceBlock();
            break;
        case 3:
            ret += intent() + "for(";
            ret += assign1.toString() + "; ";
            ret += expression.toString() + "; ";
            ret += assign2.toString() + ")" + System.lineSeparator();
            ret += startIntentBraceBlock();
            ret += statement.toString();
            ret += endIntentBraceBlock();
            break;
        default:
            break;
        }
        ret += System.lineSeparator();
        return ret;
    }
}
