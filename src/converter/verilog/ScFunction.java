package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  function  <br>
 *     ::= <b>function</b> [ range_or_type ]  name_of_function  ; <br>
 *         { tf_declaration }+ <br>
 *          statement  <br>
 *         <b>endfunction</b> 
 */
class ScFunction extends ScVerilog {
    ScRange_or_type range_type = null;
    ScName_of_function name = null;
    ArrayList<ScTf_declaration> decls = new ArrayList<ScTf_declaration>();
    ScStatement_or_null statement = null;
    public ScFunction(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFUNCTION);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScTf_declaration decl = null;
            switch(c.getId())
            {
            case ASTRANGE_OR_TYPE:
                range_type = new ScRange_or_type(c);
                break;
            case ASTNAME_OF_FUNCTION:
                name = new ScName_of_function(c);
                break;
            case ASTTF_DECLARATION:
                decl = new ScTf_declaration(c);
                decls.add(decl);
                break;
            case ASTSTATEMENT_OR_NULL:
                statement = new ScStatement_or_null(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
