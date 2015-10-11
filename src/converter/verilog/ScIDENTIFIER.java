package converter.verilog;

import java.util.ArrayList;

import parser.verilog.ASTNode;

/**
 *  identifier  <br>
 *     ::=  IDENTIFIER {. IDENTIFIER } <br>
 *     (Note: the period may <b>not</b> be preceded <b>or</b> followed by a space.) 
 */
class ScIdentifier extends ScVerilog {
    ArrayList<ScIDENTIFIER0> idens = new ArrayList<ScIDENTIFIER0>();
    public ScIdentifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTidentifier);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScIDENTIFIER0 id = null;
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                id = new ScIDENTIFIER0(c);
                idens.add(id);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret =idens.get(0).scString();
        for(int i = 1; i < idens.size(); i++) {
            ret += "." + idens.get(i).scString();
        }
        return ret;
    }
}

/**
 *  IDENTIFIER  <br>
 *     An identifier is any sequence of letters, digits, dollar signs ($), <b>and</b> <br>
 *     underscore (_) symbol, except that the first must be a letter <b>or</b> the <br>
 *     underscore; the first character may <b>not</b> be a digit <b>or</b> $. Upper <b>and</b> lower <b>case</b> <br>
 *     letters are considered to be different. Identifiers may be up to 1024 <br>
 *     characters long. Some Verilog-based tools do <b>not</b> recognize identifier <br>
 *     characters beyond the 1024th as a significant part of the identifier. Escaped <br>
 *     identifiers start with the backslash character (\) <b>and</b> may include any <br>
 *     printable ASCII character. An escaped identifier ends with white space. The <br>
 *     leading backslash character is <b>not</b> considered to be part of the identifier. 
 */
class ScIDENTIFIER0 extends ScVerilog {
    String image = "";
    public ScIDENTIFIER0(ASTNode node) {
        super(node);
        assert(node.getId() == ASTIDENTIFIER);
        image = node.firstTokenImage();
    }

    public String scString() {
        return image;
    }
}
