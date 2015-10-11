package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  concatenation  <br>
 *     ::= <b>{</b>  expression  {, expression } <b>}</b> 
 */
class ScConcatenation extends ScVerilog {
    ArrayList<ScExpression> exps = new ArrayList<ScExpression>();
    public ScConcatenation(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCONCATENATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScExpression ex = null;
            switch(c.getId())
            {
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
        ret += "(";
        for(int i = 0; i < exps.size(); i++) {
            ret += exps.get(i).scString();
            if(i < exps.size() - 1)
                ret += ", ";
        }
        ret += ")";
        return ret;
    }
}
