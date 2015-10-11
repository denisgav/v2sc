package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  block_declaration  <br>
 *     ::=  parameter_declaration  <br>
 *     ||=  reg_declaration  <br>
 *     ||=  integer_declaration  <br>
 *     ||=  real_declaration  <br>
 *     ||=  time_declaration  <br>
 *     ||=  event_declaration  
 */
class ScBlock_declaration extends ScVerilog {
    ScVerilog declaration = null;
    public ScBlock_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPARAMETER_DECLARATION:
                declaration = new ScParameter_declaration(c);
                break;
            case ASTREG_DECLARATION:
                declaration = new ScReg_declaration(c);
                break;
            case ASTINTEGER_DECLARATION:
                declaration = new ScInteger_declaration(c);
                break;
            case ASTREAL_DECLARATION:
                declaration = new ScReal_declaration(c);
                break;
            case ASTTIME_DECLARATION:
                declaration = new ScTime_declaration(c);
                break;
            case ASTEVENT_DECLARATION:
                declaration = new ScEvent_declaration(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return declaration.scString();
    }
}
