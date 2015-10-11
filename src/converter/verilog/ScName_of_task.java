package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_task  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_task extends SimpleName {
    public ScName_of_task(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_TASK);
    }
}
