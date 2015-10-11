package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> secondary_unit ::=
 *   <dd> architecture_body
 *   <br> | package_body
 */
class ScSecondary_unit extends ScVhdl {
    ScVhdl item = null;
    public ScSecondary_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSECONDARY_UNIT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTARCHITECTURE_BODY:
                item = new ScArchitecture_body(c);
                break;
            case ASTPACKAGE_BODY:
                item = new ScPackage_body(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        //return item.scString();
        return "";  // body has been add to entity or package
    }
}
