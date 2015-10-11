package converter.verilog;

import java.util.ArrayList;

import converter.IScFile;
import parser.IParser;
import parser.verilog.ASTNode;


/**
 *  source_text  <br>
 *     ::= { description } 
 */
class ScSource_text extends ScVerilog implements IScFile {
    ArrayList<ScDescription> descs = new ArrayList<ScDescription>();
    public ScSource_text(IParser parser) {
        this(parser, false);
    }
    
    /**
     * convert dividually: in order to decrease memory, <br>
     * when one standalone <b>entity</b> or <b>package</b> can be converted,<br>
     * it will be converted immediately and then it's memory will be free.<br>
     * <br>
     * "can be converted" means that:
     * <li> <b>entity</b> meets all of it's architecture body & configuration
     * <li> <b>package</b> meets it's package body
     */
    boolean convDividual = false;
    public ScSource_text(IParser parser, boolean dividual) {
        super(parser);
        convDividual = dividual;
        assert(curNode.getId() == ASTSOURCE_TEXT);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            ScDescription desc = null;
            switch(c.getId())
            {
            case ASTDESCRIPTION:
                desc = new ScDescription(c);
                descs.add(desc);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < descs.size(); i++) {
            ret += descs.toString() + System.lineSeparator();
        }
        return ret;
    }

    @Override
    public String getDeclaration() {
    	StringBuilder builder = new StringBuilder();
    	for(ScDescription d : descs)
    	{
    		builder.append(d.scString());
    	}
        return builder.toString();
    }

    @Override
    public String getImplements() {
        return "";
    }

    @Override
    public String getInclude() {
        return "";
    }

    @Override
    public void getIndividualString(StringBuffer strInclude,
            StringBuffer strDeclaration, StringBuffer strImplements) {
    }
}
