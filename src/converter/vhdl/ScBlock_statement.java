package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> block_statement ::=
 *   <dd> <i>block_</i>label :
 *   <ul> <b>block</b> [ ( <i>guard_</i>expression ) ] [ <b>is</b> ]
 *   <ul> block_header
 *   <br> block_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> block_statement_part
 *   </ul> <b>end</b> <b>block</b> [ <i>block_</i>label ] ; </ul>
 */
class ScBlock_statement extends ScCommonIdentifier implements IScStatementBlock {
    ScBlock_header header = null;
    ScBlock_declarative_part declarative_part = null;
    ScBlock_statement_part statement_part = null;
    
    String processName = "process";
    
    public ScBlock_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTBLOCK_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCK_HEADER:
                header = new ScBlock_header(c);
                break;
            case ASTBLOCK_DECLARATIVE_PART:
                declarative_part = new ScBlock_declarative_part(c);
                break;
            case ASTBLOCK_STATEMENT_PART:
                statement_part = new ScBlock_statement_part(c);
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
        processName = addMethodName("process_block_" + identifier);
    }
    
    
    private String getName() {
        return processName;
    }
    
    private String getSpec(boolean individual) {
        String ret = intent() + "void ";
        if(individual)
            ret += className + "::";
        ret += getName() + "(";
        if(param != null)
            ret += "int " + param;
        else
            ret += "void";
        ret += ")";
        return ret;
    }

    public String scString() {
        String ret = getSpec(curIndividual) + System.lineSeparator();
        ret += startIntentBraceBlock();
        ret += addLF(header.toString());
        ret += addLF(declarative_part.toString());
        ret += addLF(statement_part.toString());
        ret += endIntentBraceBlock();
        return ret;
    }

    @Override
    public String getDeclaration() {
        String ret = getSpec(false) + ";";
        return ret;
    }

    @Override
    public String getImplements() {
        return toString();
    }

    @Override
    public String getInitCode()
    {
     // just call it
        String ret = intent() + getName() + "(";
        if(param != null)
            ret += param;
        ret += ");";
        return ret;
    }
}
