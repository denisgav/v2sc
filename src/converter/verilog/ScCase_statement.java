package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  <b>case</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
 *  <b>casez</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
 *  <b>casex</b> (  expression  ) { case_item }+ <b>endcase</b>
 */
class ScCase_statement extends ScVerilog {
    ScExpression exp = null;
    ArrayList<ScCase_item> items = new ArrayList<ScCase_item>();
    public ScCase_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCASE_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScCase_item item = null;
            switch(c.getId())
            {
            case ASTEXPRESSION:
                exp = new ScExpression(c);
                break;
            case ASTCASE_ITEM:
                item = new ScCase_item(c);
                items.add(item);
                break;
            default:
                break;
            }
        }
    }
    
    public String scString() {
        String ret = intent() + "switch(";
        ret += exp.scString() + ")" + System.lineSeparator();
        ret += startIntentBraceBlock();
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).toString();
        }
        ret += endIntentBraceBlock();
        ret += System.lineSeparator();
        return ret;
    }
}
