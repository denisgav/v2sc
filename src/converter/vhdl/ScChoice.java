package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> choice ::=
 *   <dd> simple_expression
 *   <br> | discrete_range
 *   <br> | <i>element_</i>simple_name
 *   <br> | <b>others</b>
 */
class ScChoice extends ScVhdl {
    ScVhdl item = null;
    public ScChoice(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCHOICE);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSIMPLE_EXPRESSION:
            item = new ScSimple_expression(c);
            break;
        case ASTDISCRETE_RANGE:
            item = new ScDiscrete_range(c);
            break;
        case ASTIDENTIFIER:
            item = new ScSimple_name(c);
            break;
        case ASTVOID:
            item = new ScToken(c);
            break;
        default:
            break;
        }
    }
    
    public boolean isRange() {
        return (item instanceof ScDiscrete_range);
    }
    
    public boolean isOthers() {
        return (item instanceof ScToken);
    }

    public String scString() {
        return item.scString();
    }
}
