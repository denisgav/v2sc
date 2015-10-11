package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_port  <br>
 *     ::=  IDENTIFIER  
 */
class ScName_of_port extends SimpleName {
    public ScName_of_port(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_PORT);
    }
}
