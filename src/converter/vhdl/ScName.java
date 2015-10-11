package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> name ::=
 *   <dd> simple_name
 *   <br> | operator_symbol
 *   <br> | selected_name
 *   <br> | indexed_name
 *   <br> | slice_name
 *   <br> | attribute_name
 */
class ScName extends ScVhdl {
    ScVhdl item = null;
    public ScName(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTIDENTIFIER:
            item = new ScSimple_name(c);
            break;
        case ASTOPERATOR_SYMBOL:
            item = new ScOperator_symbol(c);
            break;
        case ASTSELECTED_NAME:
            item = new ScSelected_name(c);
            break;
        case ASTINDEXED_NAME:
            item = new ScIndexed_name(c);
            break;
        case ASTSLICE_NAME:
            item = new ScSlice_name(c);
            break;
        case ASTATTRIBUTE_NAME:
            item = new ScAttribute_name(c);
            break;
        default:
            break;
        }
    }
    
    public int getBitWidth() {
        return item.getBitWidth();
    }
    
    public String[] getNameSegments() {
        String[] segments = null;
        if(item instanceof ScSelected_name) {
            segments = ((ScSelected_name)item).getNameSegments();
        }else if(item instanceof ScSlice_name) {
            segments = ((ScSlice_name)item).prefix.getNameSegments();
        }else {
            segments = new String[1];
            segments[0] = item.scString();
        }
        return segments;
    }

    public String scString() {
        return item.scString();
    }
}
