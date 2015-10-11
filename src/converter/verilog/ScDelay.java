package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  delay  <br>
 *     ::= #  number  <br>
 *     ||= #  identifier  <br>
 *     ||= # ( mintypmax_expression  [, mintypmax_expression ] [, mintypmax_expression ]) 
 */
class ScDelay extends ScVerilog {
    ScNumber number = null;
    ScIdentifier id = null;
    ScMintypmax_expression exp1 = null, exp2 = null, exp3 = null;
    public ScDelay(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDELAY);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNUMBER:
                number = new ScNumber(c);
                break;
            case ASTidentifier:
                id = new ScIdentifier(c);
                break;
            case ASTMINTYPMAX_EXPRESSION:
                if(exp1 == null) {
                    exp1 = new ScMintypmax_expression(c);
                }else if(exp2 == null) {
                    exp2 = new ScMintypmax_expression(c);
                }else {
                    exp3 = new ScMintypmax_expression(c);
                }
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
