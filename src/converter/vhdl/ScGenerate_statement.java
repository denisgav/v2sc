package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> generate_statement ::=
 *   <dd> <i>generate_</i>label :
 *   <ul> generation_scheme <b>generate</b>
 *   <ul> [ {block_declarative_item }
 *   </ul> <b>begin</b> ]
 *   <ul> { architecture_statement }
 *   </ul> <b>end</b> <b>generate</b> [ <i>generate_</i>label ] ; </ul>
 */
class ScGenerate_statement extends ScCommonIdentifier implements IScStatementBlock {
    ScGeneration_scheme scheme = null;
    ScBlock_declarative_part declarative_part = null;
    ScArchitecture_statement_part statement_part = null;
    
    String processName = "process";
    public ScGenerate_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTGENERATE_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTGENERATION_SCHEME:
                scheme = new ScGeneration_scheme(c);
                break;
            case ASTBLOCK_DECLARATIVE_PART:
                declarative_part = new ScBlock_declarative_part(c);
                break;
            case ASTARCHITECTURE_STATEMENT_PART:
                statement_part = new ScArchitecture_statement_part(c);
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
        
        setStatementParam();
        processName = addMethodName("process_generate_" + identifier);
    }

    private String getName() {
        return processName;
    }
    
    private void setStatementParam() {
        if(scheme.param == null) {
            return;
        }
        
        String ident = scheme.param.identifier.scString();
        for(int i = 0; i < statement_part.items.size(); i++) {
            ScArchitecture_statement st = statement_part.items.get(i);
            if(st.statement instanceof ScSimultaneous_statement) {
                ScSimultaneous_statement sst = (ScSimultaneous_statement)st.statement;
                if(sst.item instanceof ScCommonIdentifier) {
                    ((ScCommonIdentifier)sst.item).setParam(ident);
                }
            }else {
                ScConcurrent_statement cst = (ScConcurrent_statement)st.statement;
                if(cst.item instanceof ScCommonIdentifier) {
                    ((ScCommonIdentifier)cst.item).setParam(ident);
                }
            }
        }
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
        return "";
    }

    @Override
    public String getDeclaration() {
        String ret = "";
        if(declarative_part != null)
            ret += addLF(declarative_part.getDeclaration());
        ret += addLF(statement_part.getDeclaration());
        ret += getSpec(false) + ";";
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = "";
        ret += addPrevComment();
        ret += getSpec(curIndividual) + System.lineSeparator();
        ret += startIntentBraceBlock();
        ret += addLF(scheme.toString());
        ret += startIntentBraceBlock();
        if(declarative_part != null)
            ret += addLF(declarative_part.getImplements());
        ret += statement_part.getInitCode();   // call statement part's init code
        ret += endIntentBraceBlock();
        ret += endIntentBraceBlock();
        ret += System.lineSeparator();
        
        ret += statement_part.getImplements();
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
