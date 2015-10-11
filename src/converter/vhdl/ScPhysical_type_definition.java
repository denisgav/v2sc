package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> physical_type_definition ::=
 *   <dd> range_constraint
 *   <ul> <b>units</b>
 *   <ul> primary_unit_declaration
 *   <br> { secondary_unit_declaration }
 *   </ul> <b>end</b> <b>units</b> [ <i>physical_type_</i>simple_name ] </ul>
 */
class ScPhysical_type_definition extends ScCommonIdentifier {
    ScRange_constraint range = null;
    ScVhdl primary = null;
    ArrayList<ScVhdl> secondaries = new ArrayList<ScVhdl>();
    public ScPhysical_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPHYSICAL_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTRANGE_CONSTRAINT:
                range = new ScRange_constraint(c);
                break;
            case ASTPRIMARY_UNIT_DECLARATION:
                primary = new ScPrimary_unit_declaration(c);
                break;
            case ASTSECONDARY_UNIT_DECLARATION:
                newNode =  new ScSecondary_unit_declaration(c);
                secondaries.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        warning("physical type definition not support");
        return "";
    }
}
