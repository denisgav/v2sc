package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> block_specification ::=
 *   <dd> <i>architecture_</i>name
 *   <br> | <i>block_statement_</i>label
 *   <br> | <i>generate_statement_</i>label [ ( index_specification ) ]
 */
class ScBlock_specification extends ScVhdl {
    ScVhdl name = null;
    ScVhdl index_spec = null;
    public ScBlock_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTIDENTIFIER:
                name = new ScLabel(c);
                break;
            case ASTINDEX_SPECIFICATION:
                index_spec = new ScIndex_specification(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += name.scString();
        if(index_spec != null) {
            ret += encloseBracket(index_spec.scString());   //TODO: modify here
        }
        return ret;
    }
}
