package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_class ::=
 *   <dd> <b>entity</b>
 *   <br> | <b>architecture</b>
 *   <br> | <b>configuration</b>
 *   <br> | <b>procedure</b>
 *   <br> | <b>function</b>
 *   <br> | <b>package</b>
 *   <br> | <b>type</b>
 *   <br> | <b>subtype</b>
 *   <br> | <b>constant</b>
 *   <br> | <b>signal</b>
 *   <br> | <b>variable</b>
 *   <br> | <b>component</b>
 *   <br> | <b>label</b>
 *   <br> | <b>literal</b>
 *   <br> | <b>units</b>
 *   <br> | <b>group</b>
 *   <br> | <b>file</b>
 *   <br> | <b>nature</b>
 *   <br> | <b>subnature</b>
 *   <br> | <b>quantity</b>
 *   <br> | <b>terminal</b>
 */
class ScEntity_class extends ScVhdl {
    String image = "";
    public ScEntity_class(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTENTITY_CLASS);
        image = node.firstTokenImage();
    }

    public String scString() {
        return "";
    }
}
