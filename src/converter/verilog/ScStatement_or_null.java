package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  statement_or_null  <br>
 *     ::=  statement  <br>
 *     ||= ; 
 */
class ScStatement_or_null extends ScVerilog {
    ScVerilog item = null;
    public ScStatement_or_null(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTATEMENT_OR_NULL);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTSTATEMENT:
                item = new ScStatement(c);
                break;
            case ASTNULL:
                item = new ScNULL(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(item instanceof ScNULL) {
            ret += ";" + System.lineSeparator();
        }else {
            ret += item.toString();
        }
        return ret;
    }
}
