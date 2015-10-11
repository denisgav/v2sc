package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  case_item  <br>
 *     ::=  expression  {, expression } :  statement_or_null  <br>
 *     ||= <b>default</b> :  statement_or_null  <br>
 *     ||= <b>default</b>  statement_or_null  
 */
class ScCase_item extends ScVerilog {
    ArrayList<ScExpression> exps = new ArrayList<ScExpression>();
    ScStatement_or_null statement = null;
    public ScCase_item(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCASE_ITEM);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScExpression ex = null;
            switch(c.getId())
            {
            case ASTSTATEMENT_OR_NULL:
                statement = new ScStatement_or_null(c);
                break;
            case ASTEXPRESSION:
                ex = new ScExpression(c);
                exps.add(ex);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(exps.size() > 0) {
            for(int i = 0; i < exps.size(); i++) {
                ret += exps.get(i).addPrevComment();
                ret += intent() + "case " + exps.get(i).scString() + ":";
                ret += exps.get(i).addPostComment() + System.lineSeparator();
            }
        }else {
            ret += intent() + "default:" + System.lineSeparator();
        }
        startIntentBlock();
        ret += statement.toString();
        ret += "break;" + System.lineSeparator();
        endIntentBlock();
        return ret;
    }
}
