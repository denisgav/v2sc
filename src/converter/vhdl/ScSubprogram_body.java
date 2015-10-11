package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> subprogram_body ::=
 *   <dd> subprogram_specification <b>is</b>
 *   <ul> subprogram_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> subprogram_statement_part
 *   </ul> <b>end</b> [ subprogram_kind ] [ designator ] ;
 */
class ScSubprogram_body extends ScVhdl implements IScStatementBlock {
    ScSubprogram_specification spec = null;
    ScSubprogram_declarative_part declarative_part = null;
    ScSubprogram_statement_part statement_part = null;
    public ScSubprogram_body(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSUBPROGRAM_BODY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBPROGRAM_SPECIFICATION:
                spec = new ScSubprogram_specification(c);
                break;
            case ASTSUBPROGRAM_DECLARATIVE_PART:
                declarative_part = new ScSubprogram_declarative_part(c);
                break;
            case ASTSUBPROGRAM_STATEMENT_PART:
                statement_part = new ScSubprogram_statement_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += spec.specString(true) + System.lineSeparator();

        ret += startIntentBraceBlock();
        ret += addLF(declarative_part.toString());
        String[] lvars = declarative_part.getLoopVar();
        if(lvars != null && lvars.length > 0) {
            ret += intent() + "int ";
            for(int i = 0; i < lvars.length; i++) {
                ret += lvars[i];
                if(i < lvars.length - 1) {
                    ret += ", ";
                }
            }
            ret += ";" + System.lineSeparator();
        }
        ret += System.lineSeparator();
        ret += addLF(statement_part.toString());
        ret += endIntentBraceBlock();
        return ret;
    }

    @Override
    public String getDeclaration() {
        String ret = spec.specString(false) + ";" + System.lineSeparator();
        return ret;
    }

    @Override
    public String getImplements() {
        return toString();
    }

    @Override
    public String getInitCode() {
        return "";
    }
}
