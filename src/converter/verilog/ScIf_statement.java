package converter.verilog;

import parser.IASTNode;
import parser.verilog.ASTNode;

/**
 *  <b>if</b> (  expression  )  statement_or_null  <br>
 *  <b>if</b> (  expression  )  statement_or_null  <b>else</b>  statement_or_null
 */
class ScIf_statement extends ScVerilog {
    ScExpression condition = null;
    ScStatement_or_null statement = null;
    ScStatement_or_null else_statement = null;
    public ScIf_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTIF_STATEMENT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScExpression(c);
                break;
            case ASTSTATEMENT_OR_NULL:
                if(statement == null) {
                    statement = new ScStatement_or_null(c);
                }else {
                    else_statement = new ScStatement_or_null(c);
                }
                break;
            default:
                break;
            }
        }
    }
    
    boolean isGrandParentElseStatement() {
        IASTNode grandParent = curNode.getParent().getParent();
        if(grandParent == null) {
            return false;
        }
        
        if(grandParent.getId() != ASTSTATEMENT_OR_NULL) {
            return false;
        }
        
        IASTNode grandgrand = grandParent.getParent();
        if(grandgrand.getId() == ASTIF_STATEMENT) {
            return false;
        }
        
        // if-else -> statement_or_null -> statement -> me
        int count = 0;
        for(int i = 0; i < grandgrand.getChildrenNum(); i++) {
            if(grandgrand.getChild(0).getId() == ASTSTATEMENT_OR_NULL) {
                if(count == 1) {    // second statement_or_null is in else
                    return true;
                }
                count ++;
            }
        }
        return false;
    }
    
    boolean isElseGrandChildrenStatement() {
        if(else_statement == null) {
            return false;
        }
        
        IASTNode child = else_statement.curNode.getChild(0);
        if(child == null) {
            return false;
        }
        IASTNode grandChild = child.getChild(0);
        if(grandChild.getId() == ASTIF_STATEMENT) {
            // me-else -> statement_or_null -> statement -> if
            return true;
        }
        return false;
    }
    
    public String scString() {
        String ret = "";
        if(isGrandParentElseStatement()) {
            ret += " if(";
        }else {
            ret += intent() + "if(";
        }
        ret += condition.scString() + ")" + System.lineSeparator();
        ret += startIntentBraceBlock();
        ret += statement.toString();
        ret += endIntentBraceBlock();
        if(else_statement != null) {
            ret += System.lineSeparator();
            ret += "else";
            if(!isElseGrandChildrenStatement()) {
                ret += System.lineSeparator();
                ret += startIntentBraceBlock();
                ret += statement.toString();
                ret += endIntentBraceBlock();
            }else {
                ret += statement.toString();
            }
        }
        ret += System.lineSeparator();
        return ret;
    }
}
