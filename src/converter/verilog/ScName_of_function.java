package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_function  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_function extends SimpleName {
    public ScName_of_function(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_FUNCTION);
    }
}
