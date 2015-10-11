package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  combinational_entry  <br>
 *     ::=  level_input_list  :  output_symbol  ; 
 */
class ScCombinational_entry extends ScVerilog {
    ScLevel_input_list input = null;
    ScOutput_symbol output = null;
    public ScCombinational_entry(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCOMBINATIONAL_ENTRY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTLEVEL_INPUT_LIST:
                input = new ScLevel_input_list(c);
                break;
            case ASTOUTPUT_SYMBOL:
                output = new ScOutput_symbol(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += input.scString() + ":";
        ret += output.scString() + ";" + System.lineSeparator();
        return ret;
    }
}
