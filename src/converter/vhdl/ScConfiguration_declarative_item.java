package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> configuration_declarative_item ::=
 *   <dd> use_clause
 *   <br> | attribute_specification
 *   <br> | group_declaration
 */
class ScConfiguration_declarative_item extends ScVhdl {
    ScVhdl item = null;
    public ScConfiguration_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTCONFIGURATION_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTUSE_CLAUSE:
            item = new ScUse_clause(node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new ScAttribute_specification(node);
            break;
        case ASTGROUP_DECLARATION:
            item = new ScGroup_declaration(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}
