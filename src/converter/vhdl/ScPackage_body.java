package converter.vhdl;

import common.MyDebug;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> package_body ::=
 *   <dd> <b>package body</b> <i>package_</i>simple_name <b>is</b>
 *   <ul> package_body_declarative_part
 *   </ul> <b>end</b> [ <b>package body</b> ] [ <i>package_</i>simple_name ] ;
 */
class ScPackage_body extends ScCommonIdentifier implements IScStatementBlock {
    ScName package_name = null;
    ScPackage_body_declarative_part declarative_part = null;
    public ScPackage_body(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTPACKAGE_BODY);
        int i;
        for(i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int id = c.getId();
            ScVhdl tmp = null;
            switch(id)
            {
            case ASTIDENTIFIER:
                tmp = new ScIdentifier(c);
                identifier = tmp.scString();
                break;
            case ASTPACKAGE_BODY_DECLARATIVE_PART:
                declarative_part = new ScPackage_body_declarative_part(c);
                break;
            default:
                break;
            }
        }
        assert(identifier != null);
        for(i = 0; i < units.size(); i++) {
            ScCommonIdentifier ident = units.get(i);
            if(ident instanceof ScPackage_declaration
                && ident.identifier.equalsIgnoreCase(identifier)) {
                ((ScPackage_declaration)ident).setPackageBody(this);
                break;
            }
        }
        if(i == units.size()) {
            MyDebug.printFileLine("package boty no corresponding package");
        }
    }

    public String scString() {
        String ret = "";
        ret += addLF(declarative_part.toString());
        return ret;
    }

    @Override
    public String getDeclaration() {
        String ret = "";
        ret += addLF(declarative_part.getDeclaration());
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = "";
        ret += addLF(declarative_part.getImplements());
        return ret;
    }

    @Override
    public String getInitCode() {
        String ret = "";
        return ret;
    }
}
