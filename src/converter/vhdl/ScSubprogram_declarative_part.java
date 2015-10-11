package converter.vhdl;

import java.util.ArrayList;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> subprogram_declarative_part ::=
 *   <dd> { subprogram_declarative_item }
 */
class ScSubprogram_declarative_part extends ScVhdl implements IScStatementBlock {
    ArrayList<ScSubprogram_declarative_item> items = new ArrayList<ScSubprogram_declarative_item>();
    public ScSubprogram_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            items.add(new ScSubprogram_declarative_item(c));
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).toString();
            if(i < items.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }
    
    @Override
    public String getDeclaration() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).item instanceof ScSubprogram_body)
                ret += ((ScSubprogram_body)items.get(i).item).getDeclaration() + System.lineSeparator();
        }
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).item instanceof ScSubprogram_body)
                ret += ((ScSubprogram_body)items.get(i).item).getImplements();
            else
                ret += items.get(i).toString();
            if(i < items.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }

    @Override
    public String getInitCode() {
        return "";
    }
}
