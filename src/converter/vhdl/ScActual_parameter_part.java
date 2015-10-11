package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> actual_parameter_part ::=
 *   <dd> <i>parameter_</i>association_list
 */
class ScActual_parameter_part extends ScVhdl {
    ScAssociation_list paramList = null;
    public ScActual_parameter_part(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTACTUAL_PARAMETER_PART);
        paramList = new ScAssociation_list(node);
    }

    public String scString() {
        return paramList.scString();
    }
}
