package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> attribute_designator ::=
 *   <dd> <i>attribute_</i>simple_name
 */
class ScAttribute_designator extends ScVhdl {
    ScVhdl name = null;
    public ScAttribute_designator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTATTRIBUTE_DESIGNATOR);
        name = new ScSimple_name(node);
    }

    public String scString() {
        return name.scString();
    }
}
