package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  simple name ::= IDENTIFIER  
 */
class SimpleName extends ScVerilog {
    ScIDENTIFIER0 id = null;
    public SimpleName(ASTNode node) {
        super(node);
        //id = new ScIDENTIFIER0((ASTNode)node.getChild(0));
        id = new ScIDENTIFIER0(node);
    }

    public String scString() {
        return id.toString();
    }
}
