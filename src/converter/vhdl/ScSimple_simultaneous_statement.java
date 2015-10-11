package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> simple_simultaneous_statement ::=
 *   <dd> [ label : ] simple_expression == simple_expression [ tolerance_aspect ] ;
 */
class ScSimple_simultaneous_statement extends ScCommonIdentifier implements IScStatementBlock {
    ScSimple_expression exp1 = null;
    ScSimple_expression exp2 = null;
    ScTolerance_aspect tolerance = null;
    public ScSimple_simultaneous_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSIMPLE_SIMULTANEOUS_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                break;
            case ASTSIMPLE_EXPRESSION:
                if(exp1 == null) {
                    exp1 = new ScSimple_expression(c);
                }else {
                    exp2 = new ScSimple_expression(c);
                }
                break;
            case ASTTOLERANCE_ASPECT:
                tolerance = new ScTolerance_aspect(c);
                break;
           default:
                break;
            }
        }
        if(identifier.isEmpty())
            identifier = String.format("line%d", node.getFirstToken().beginLine);
    }

    public String scString() {
        warning("simple_simultaneous_statement not support");
        return "";
    }

    @Override
    public String getDeclaration()
    {
        return "";
    }

    @Override
    public String getImplements()
    {
        return "";
    }

    @Override
    public String getInitCode()
    {
        return toString();
    }
}
