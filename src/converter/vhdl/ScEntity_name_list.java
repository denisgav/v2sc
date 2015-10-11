package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> entity_name_list ::=
 *   <dd> entity_designator { , entity_designator }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class ScEntity_name_list extends ScVhdl {
    public ScEntity_name_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_NAME_LIST);
    }

    public String scString() {
        return "";
    }
}
