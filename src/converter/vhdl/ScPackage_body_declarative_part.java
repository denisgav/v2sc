package converter.vhdl;

import java.util.ArrayList;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> package_body_declarative_part ::=
 *   <dd> { package_body_declarative_item }
 */
class ScPackage_body_declarative_part extends ScVhdl implements IScStatementBlock {
    ArrayList<ScPackage_body_declarative_item> items = new ArrayList<ScPackage_body_declarative_item>();
    public ScPackage_body_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPACKAGE_BODY_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            items.add(new ScPackage_body_declarative_item(c));
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }
    
    @Override
    public String getDeclaration() {
        String ret = "";
        //for(int i = 0; i < items.size(); i++) {
        //    if(items.get(i).item instanceof ScSubprogram_body)
        //        ret += ((ScSubprogram_body)items.get(i).item).getDeclaration();
        //}
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).item instanceof ScSubprogram_body)
                ret += ((ScSubprogram_body)items.get(i).item).getImplements() + System.lineSeparator();
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
