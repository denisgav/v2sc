package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  module_instantiation  <br>
 *     ::=  name_of_module  [ parameter_value_assignment ] <br>
 *          module_instance  {, module_instance } ; 
 */
class ScModule_instantiation extends ScVerilog {
    ScName_of_module moduleName = null;
    ScParameter_value_assignment param_assign = null;
    ArrayList<ScModule_instance> modules = new ArrayList<ScModule_instance>();
    public ScModule_instantiation(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE_INSTANTIATION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScModule_instance module  = null;
            switch(c.getId())
            {
            case ASTNAME_OF_MODULE:
                moduleName = new ScName_of_module(c);
                break;
            case ASTPARAMETER_VALUE_ASSIGNMENT:
                param_assign = new ScParameter_value_assignment(c);
                break;
            case ASTMODULE_INSTANCE:
                module = new ScModule_instance(c);
                modules.add(module);
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
