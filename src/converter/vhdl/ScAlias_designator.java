package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> alias_designator ::=
 *   <dd> identifier
 *   <br> | character_literal
 *   <br> | operator_symbol
 */
class ScAlias_designator extends ScVhdl {
    ScVhdl subNode = null;
    public ScAlias_designator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALIAS_DESIGNATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        if(id != ASTIDENTIFIER) {
            warning("only support identifier alias");
        }
        switch(id)
        {
        case ASTIDENTIFIER:
            subNode = new ScIdentifier(c);
            break;
        case ASTVOID:   // character literal
            subNode = new ScToken(c);
            break;
        case ASTOPERATOR_SYMBOL:
            subNode = new ScOperator_symbol(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return subNode.scString();
    }
}
