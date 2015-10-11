package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  list_of_variables  <br>
 *     ::=  name_of_variable  {, name_of_variable } 
 */
class ScList_of_variables extends ScVerilog {
    ArrayList<ScName_of_variable> names = new ArrayList<ScName_of_variable>();
    public ScList_of_variables(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIST_OF_VARIABLES);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScName_of_variable name = null;
            switch(c.getId())
            {
            case ASTNAME_OF_VARIABLE:
                name = new ScName_of_variable(c);
                names.add(name);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < names.size(); i++) {
            ret += names.get(i).scString();
            if(i < names.size() - 1) {
                ret += ", ";
            }
        }
        return ret;
    }
}
