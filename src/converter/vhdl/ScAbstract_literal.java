package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> abstract_literal ::=
 *   <dd> decimal_literal | based_literal
 */
class ScAbstract_literal extends ScVhdl {
    ScVhdl item = null;
    public ScAbstract_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTABSTRACT_LITERAL);
        int kind = node.getFirstToken().kind;
        if(kind == decimal_literal) {
            item = new ScDecimal_literal(node);
        }else if(kind == based_literal) {
            item = new ScBased_literal(node);
        }
    }

    public String scString() {
        return item.scString();
    }
}
