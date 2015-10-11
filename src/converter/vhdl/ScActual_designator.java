package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> actual_designator ::=
 *   <dd> expression
 *   <br> | <i>signal_</i>name
 *   <br> | <i>variable_</i>name
 *   <br> | <i>file_</i>name
 *   <br> | <i>terminal_</i>name
 *   <br> | <i>quantity_</i>name
 *   <br> | <b>open</b>
 */
class ScActual_designator extends ScVhdl {
    ScVhdl subNode = null;
    boolean isOpen = false;
    public ScActual_designator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACTUAL_DESIGNATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTEXPRESSION:
            subNode = new ScExpression(c);
            break;
        case ASTVOID:
            isOpen = true;
            break;
        case ASTNAME:
            subNode = new ScName(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        String ret = "";
        if(subNode != null)
            ret = subNode.scString();
        else if(isOpen) {
            warning("token open");
            ret = "null";
        }
        return ret;
    }
}
