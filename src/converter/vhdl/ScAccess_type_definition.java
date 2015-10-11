package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> access_type_definition ::=
 *   <dd> <b>access</b> subtype_indication
 */
class ScAccess_type_definition extends ScCommonIdentifier {
    ScVhdl sub = null;
    public ScAccess_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACCESS_TYPE_DEFINITION);
        sub = new ScSubtype_indication((ASTNode)node.getChild(0));
    }

    public String scString() {
        warning("token access ignored");
        return intent() + sub.scString() + " " + identifier;
    }
}
