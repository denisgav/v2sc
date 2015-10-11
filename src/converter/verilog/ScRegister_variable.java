package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  register_variable  <br>
 *     ::=  name_of_register  <br>
 *     ||=  name_of_memory  range 
 */
class ScRegister_variable extends ScVerilog {
    ScVerilog name = null;
    ScRange range;
    public ScRegister_variable(ASTNode node) {
        super(node);
        assert(node.getId() == ASTREGISTER_VARIABLE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTNAME_OF_REGISTER:
                name = new ScName_of_register(c);
                break;
            case ASTNAME_OF_MEMORY:
                name = new ScName_of_memory(c);
                break;
            case ASTRANGE:
                range = new ScRange(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
