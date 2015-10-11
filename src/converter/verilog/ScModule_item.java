package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  module_item  <br>
 *     ::=  parameter_declaration  <br>
 *     ||=  input_declaration  <br>
 *     ||=  output_declaration  <br>
 *     ||=  inout_declaration  <br>
 *     ||=  net_declaration  <br>
 *     ||=  reg_declaration  <br>
 *     ||=  time_declaration  <br>
 *     ||=  integer_declaration  <br>
 *     ||=  real_declaration  <br>
 *     ||=  event_declaration  <br>
 *     ||=  gate_declaration  <br>
 *     ||=  udp_instantiation  <br>
 *     ||=  module_instantiation  <br>
 *     ||=  parameter_override  <br>
 *     ||=  continuous_assign  <br>
 *     ||=  specify_block  <br>
 *     ||=  initial_statement  <br>
 *     ||=  always_statement  <br>
 *     ||=  task  <br>
 *     ||=  function  
 */
class ScModule_item extends ScVerilog {
    ScVerilog item = null;
    public ScModule_item(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE_ITEM);
        ASTNode c = (ASTNode)curNode.getChild(0);
        switch(c.getId())
        {
        case ASTPARAMETER_DECLARATION:
            item = new ScParameter_declaration(c);
            break;
        case ASTINPUT_DECLARATION:
            item = new ScInput_declaration(c);
            break;
        case ASTOUTPUT_DECLARATION:
            item = new ScOutput_declaration(c);
            break;
        case ASTINOUT_DECLARATION:
            item = new ScInout_declaration(c);
            break;
        case ASTNET_DECLARATION:
            item = new ScNet_declaration(c);
            break;
        case ASTREG_DECLARATION:
            item = new ScReg_declaration(c);
            break;
        case ASTTIME_DECLARATION:
            item = new ScTime_declaration(c);
            break;
        case ASTINTEGER_DECLARATION:
            item = new ScInteger_declaration(c);
            break;
        case ASTREAL_DECLARATION:
            item = new ScReal_declaration(c);
            break;
        case ASTEVENT_DECLARATION:
            item = new ScUdp_instantiation(c);
            break;
        case ASTGATE_DECLARATION:
            item = new ScEvent_declaration(c);
            break;
        case ASTUDP_INSTANTIATION:
            item = new ScGate_declaration(c);
            break;
        case ASTMODULE_INSTANTIATION:
            item = new ScModule_instantiation(c);
            break;
        case ASTPARAMETER_OVERRIDE:
            item = new ScParameter_override(c);
            break;
        case ASTCONTINUOUS_ASSIGN:
            item = new ScContinuous_assign(c);
            break;
        case ASTSPECIFY_BLOCK:
            item = new ScSpecify_block(c);
            break;
        case ASTINITIAL_STATEMENT:
            item = new ScInitial_statement(c);
            break;
        case ASTALWAYS_STATEMENT:
            item = new ScAlways_statement(c);
            break;
        case ASTTASK:
            item = new ScTask(c);
            break;
        case ASTFUNCTION:
            item = new ScFunction(c);
            break;
        default:
            break;
        }
    }
    
    public boolean isParameter() {
        return (item instanceof ScParameter_declaration);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
