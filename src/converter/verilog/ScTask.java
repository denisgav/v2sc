package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  task  <br>
 *     ::= <b>task</b>  name_of_task  ; <br>
 *         { tf_declaration } <br>
 *          statement_or_null  <br>
 *         <b>endtask</b> 
 */
class ScTask extends ScVerilog {
    ScName_of_task name = null;
    ArrayList<ScTf_declaration> decls = new ArrayList<ScTf_declaration>();
    ScStatement_or_null statement = null;
    public ScTask(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTASK);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScTf_declaration decl = null;
            switch(c.getId())
            {
            case ASTNAME_OF_TASK:
                name = new ScName_of_task(c);
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
