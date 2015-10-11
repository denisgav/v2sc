package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  seq_block  <br>
 *     ::= <b>begin</b> { statement } <b>end</b> <br>
 *     ||= <b>begin</b> :  name_of_block  { block_declaration } { statement } <b>end</b> 
 */
class ScSeq_block extends ScVerilog {
    public ScSeq_block(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSEQ_BLOCK);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
