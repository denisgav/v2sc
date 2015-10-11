package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> association_element ::=
 *   <dd> [ formal_part => ] actual_part
 */
class ScAssociation_element extends ScVhdl {
    ScFormal_part formal_part = null;
    ScActual_part actual_part = null;
    public ScAssociation_element(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSOCIATION_ELEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTFORMAL_PART:
                formal_part = new ScFormal_part(c);
                break;
            case ASTACTUAL_PART:
                actual_part = new ScActual_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(formal_part != null) {
            ret += formal_part.scString();
            ret += encloseBracket(actual_part.scString());   //TODO write() ??
        }else {
            ret += actual_part.scString();
        }
        return ret;
    }
}
