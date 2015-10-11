package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  number  <br>
 *     ::=  decimal_number  <br>
 *     ||= [ unsigned_number ]  base   unsigned_number  <br>
 *     ||=  decimal_number . unsigned_number  <br>
 *     ||=  decimal_number [. unsigned_number ] <br>
 *         E decimal_number  <br>
 *     ||=  decimal_number [. unsigned_number ] <br>
 *         e decimal_number  <br>
 *     (Note: embedded spaces are illegal in Verilog numbers, but embedded underscore <br>
 *     characters can be used <b>for</b> spacing in any type of number.) 
 */
class ScNumber extends ScVerilog {
    public ScNumber(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNUMBER);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
