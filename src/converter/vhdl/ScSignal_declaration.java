package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> signal_declaration ::=
 *   <dd> <b>signal</b> identifier_list : subtype_indication [ signal_kind ] [ := expression ] ;
 */
class ScSignal_declaration extends ScCommonDeclaration {
    ScSignal_kind signal_kind = null;
    public ScSignal_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSIGNAL_DECLARATION);
    }
    
    public String scString() {
        String ret = intent() + super.scString();
        ret += ";";
        return ret;
    }
}
