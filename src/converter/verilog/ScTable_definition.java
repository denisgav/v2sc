package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  table_definition  <br>
 *     ::= <b>table</b>  table_entries  <b>endtable</b> 
 */
class ScTable_definition extends ScVerilog {
    public ScTable_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTABLE_DEFINITION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
