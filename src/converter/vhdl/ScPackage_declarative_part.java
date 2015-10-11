package converter.vhdl;

import java.util.ArrayList;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> package_declarative_part ::=
 *   <dd> { package_declarative_item }
 */
class ScPackage_declarative_part extends ScVhdl implements IScStatementBlock {
    ArrayList<ScPackage_declarative_item> items = new ArrayList<ScPackage_declarative_item>();
    public ScPackage_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPACKAGE_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScPackage_declarative_item item = new ScPackage_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            //if(items.get(i).item instanceof ScComponent_declaration)
            //    continue;   // component declaration in other file
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
            //if(items.get(i).item instanceof ScComponent_declaration)
            //    continue;   // component declaration in other file
            if(items.get(i).item instanceof ScSubprogram_body)
                ret += ((ScSubprogram_body)items.get(i).item).getDeclaration();
            else
                ret += items.get(i).toString();
            if(i < items.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).item instanceof ScSubprogram_body)
                ret += ((ScSubprogram_body)items.get(i).item).getImplements() + System.lineSeparator();
        }
        return ret;
    }

    @Override
    public String getInitCode() {
        return "";
    }
}
