package converter.vhdl;

import java.util.ArrayList;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> entity_declaration ::=
 *   <dd> <b>entity</b> identifier <b>is</b>
 *   <ul> entity_header
 *   <br> entity_declarative_part
 *   </ul> [ <b>begin</b>
 *   <ul> entity_statement_part ]
 *   </ul> <b>end</b> [ <b>entity</b> ] [ <i>entity_</i>simple_name ] ;
 */
class ScEntity_declaration extends ScCommonIdentifier implements IScStatementBlock {
    ScEntity_header header = null;
    ScEntity_declarative_part declarative_part = null;
    ScEntity_statement_part statement_part = null;
    ScArchitecture_body body = null;
    ScConfiguration_declaration config = null;
    
    ArrayList<String> processNameArray = new ArrayList<String>();
    
    public ScEntity_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTENTITY_DECLARATION);
        curLevel = 0;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl tmp = null;
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                tmp = new ScIdentifier(c);
                identifier = tmp.scString();
                break;
            case ASTENTITY_HEADER:
                header = new ScEntity_header(c);
                break;
            case ASTENTITY_DECLARATIVE_PART:
                declarative_part = new ScEntity_declarative_part(c);
                break;
            case ASTENTITY_STATEMENT_PART:
                statement_part = new ScEntity_statement_part(c);
                break;
            default:
                break;
            }
        }
        units.add(this);
    }

    public String scString() {
        String ret = System.lineSeparator();
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        if(header.generic != null) {
            ret += "template<" + System.lineSeparator();
            startIntentBlock();
            ret += header.generic.toString();
            endIntentBlock();
            ret += System.lineSeparator() + ">" + System.lineSeparator();
        }
        ret += "SC_MODULE(" + getName() + ")" + System.lineSeparator() + "{" + System.lineSeparator();
        startIntentBlock();
        ret += addLF(header.toString());
        ret += addLF(declarative_part.toString());
        ret += addLF(body.getDeclaration());
        
        ret += getInitCode();
        ret += getImplements();
        endIntentBlock();
        ret += "};" + System.lineSeparator();
        return ret;
    }
    
    public void setArchitectureBody(ScArchitecture_body body) {
        this.body = body;
    }
    
    public void setConfigurationDeclaration(ScConfiguration_declaration config) {
        this.config = config;
    }
    
    public String getName() {
        return identifier;
    }

    @Override
    public String getInitCode()
    {
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        String ret = "";
        className = getName();
        ret += intent() + "SC_CTOR(" + getName() + ")" + System.lineSeparator();
        ret += startIntentBraceBlock();
        ret += body.getInitCode();
        ret += endIntentBraceBlock();
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        String ret = System.lineSeparator();
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        
        ret += addPrevComment();
        
        className = getName();
        if(header.generic != null) {
            
            curIndividual = false;
            
            ret += "template<" + System.lineSeparator();
            startIntentBlock();
            ret += header.generic.toString();
            endIntentBlock();
            ret += System.lineSeparator() + ">" + System.lineSeparator();
        }
        ret += "SC_MODULE(" + getName() + ")" + System.lineSeparator() + "{" + System.lineSeparator();
        startIntentBlock();
        ret += addLF(header.toString());
        ret += addLF(declarative_part.getDeclaration());
        ret += addLF(body.getDeclaration());
        ret += System.lineSeparator() + addLF(getInitCode());   // add sc_ctor
        
        if(!curIndividual) {
            // getImplements
            ret += System.lineSeparator() + System.lineSeparator();
            ret += addLF(declarative_part.getImplements());
            if(statement_part != null) {
                ret += addLF(statement_part.toString());
            }
            ret += addLF(body.getImplements());
        }
        
        endIntentBlock();
        ret += "};" + System.lineSeparator();
        
        return ret;
    }

    @Override
    public String getImplements()
    {
        String ret = "";
        if(body == null || !curIndividual) {    // template class can only in one file
            return "";  //TODO no entity body, ignore
        }
        className = getName();
        ret += addLF(declarative_part.getImplements());
        if(statement_part != null) {
            ret += addLF(statement_part.toString());
        }
        ret += addLF(body.getImplements());
        return ret;
    }
    
    protected boolean isProcessNameExist(String name)
    {
        for(int i = 0; i < processNameArray.size(); i++)
        {
            if(processNameArray.get(i).equals(name))
                return true;
        }
        return false;
    }
    
    protected String addProcessName(String name)
    {
        if(name == null || name.isEmpty())
            return "";
        
        String ret = name;
        while(isProcessNameExist(ret))
        {
            int len = ret.length();
            int i = len - 1;
            while(i >= 0 && Character.isDigit(ret.charAt(i))) i--;
            assert(i >= 0);
            int num = -1;
            if(i < len - 1)
                num = Integer.parseInt(ret.substring(i+1));
            ret = ret.substring(0, i+1) + (num+1);
        }
        processNameArray.add(ret);
        return ret;
    }
}
