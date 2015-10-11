package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  list_of_register_variables  <br>
 *     ::=  register_variable  {, register_variable } 
 */
class ScList_of_register_variables extends ScVerilog {
    ArrayList<ScRegister_variable> vars = new ArrayList<ScRegister_variable>();
    public ScList_of_register_variables(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_REGISTER_VARIABLES);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScRegister_variable var = null;
            switch(c.getId())
            {
            case ASTREGISTER_VARIABLE:
                var = new ScRegister_variable(c);
                vars.add(var);
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
