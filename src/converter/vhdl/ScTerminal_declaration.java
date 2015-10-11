package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> terminal_declaration ::=
 *   <dd> <b>terminal</b> identifier_list : subnature_indication ;
 */
class ScTerminal_declaration extends ScCommonDeclaration {
    public ScTerminal_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTTERMINAL_DECLARATION);
    }
    
    public String scString() {
        String ret = intent() + super.scString();
        ret += ";";
        return ret;
    }
}
