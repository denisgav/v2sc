package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_procedural_statement ::=
 *   <dd> [ <i>procedural_</i>label : ]
 *   <ul> <b>procedural</b> [ <b>is</b> ]
 *   <ul> procedural_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> procedural_statement_part
 *   </ul> <b>end</b> <b>procedural</b> [ <i>procedural_</i>label ] ; </ul>
 */
class ScSimultaneous_procedural_statement extends ScCommonIdentifier implements IScStatementBlock {
    ScProcedural_declarative_part declarative_part = null;
    ScProcedural_statement_part statement_part = null;
    
    String processName = "process";
    public ScSimultaneous_procedural_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSIMULTANEOUS_PROCEDURAL_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPROCEDURAL_DECLARATIVE_PART:
                declarative_part = new ScProcedural_declarative_part(c);
                break;
            case ASTPROCEDURAL_STATEMENT_PART:
                statement_part = new ScProcedural_statement_part(c);
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
        processName = addMethodName("process_procedural_" + identifier);
    }
    
    private String getName() {
        return processName;
    }
    
    private String getSpec() {
        String ret = intent() + "void ";
        ret += getName() + "(";
        if(param != null)
            ret += "int " + param;
        else
            ret += "void";
        ret += ")";
        return ret;
    }

    public String scString() {
        String ret = getSpec() + System.lineSeparator();

        ret += startIntentBraceBlock();
        ret += addLFIntent(declarative_part.toString());
        ret += addLFIntent(statement_part.toString());
        ret += endIntentBraceBlock();
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        String ret = "";
        ret += addLF(declarative_part.getDeclaration());
        ret += getSpec() + ";";
        return ret;
    }

    @Override
    public String getImplements()
    {
        String ret = getSpec() + System.lineSeparator();
        ret += startIntentBraceBlock();
        ret += addLFIntent(declarative_part.getImplements());
        ret += addLFIntent(statement_part.toString());
        ret += endIntentBraceBlock();
        return ret;
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
