package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  table_entries  <br>
 *     ::= { combinational_entry }+ <br>
 *     ||= { sequential_entry }+ 
 */
class ScTable_entries extends ScVerilog {
    public ScTable_entries(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTABLE_ENTRIES);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
