package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> concurrent_assertion_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] assertion ;
 */
class ScConcurrent_assertion_statement extends ScCommonIdentifier implements IScStatementBlock {
    ScAssertion assertion = null;
    public ScConcurrent_assertion_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCONCURRENT_ASSERTION_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTASSERTION:
                assertion = new ScAssertion(c);
                break;
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                break;
            default:
                break;
            }
        }
        if(identifier.isEmpty())
            identifier = String.format("line%d", node.getFirstToken().beginLine);
    }

    public String scString() {
        return assertion.scString() + ";";
    }

    @Override
    public String getDeclaration() {
        return "";
    }

    @Override
    public String getImplements() {
        return "";
    }

    @Override
    public String getInitCode()
    {
        return toString();
    }
}
