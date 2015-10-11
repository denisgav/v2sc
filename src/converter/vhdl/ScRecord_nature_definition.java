package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> record_nature_definition ::=
 *   <dd> <b>record</b>
 *   <ul> nature_element_declaration
 *   <br> { nature_element_declaration }
 *   </ul><b>end</b> <b>record</b> [ <i>record_nature_</i>simple_name ]
 */
class ScRecord_nature_definition extends ScVhdl {
    public ScRecord_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRECORD_NATURE_DEFINITION);
    }

    public String scString() {
        return "";
    }
}
