package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> suffix ::=
 *   <dd> simple_name
 *   <br> | character_literal
 *   <br> | operator_symbol
 *   <br> | <b>all</b>
 */
class ScSuffix extends ScVhdl {
    ScVhdl item = null;
    public ScSuffix(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUFFIX);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTIDENTIFIER:
            item = new ScSimple_name(c);
            break;
        case ASTOPERATOR_SYMBOL:
            item = new ScOperator_symbol(c);
            break;
        case ASTVOID:
            item = new ScToken(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}
