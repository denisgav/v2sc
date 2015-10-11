package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  specify_item  <br>
 *     ::=  specparam_declaration  <br>
 *     ||=  path_declaration  <br>
 *     ||=  level_sensitive_path_declaration  <br>
 *     ||=  edge_sensitive_path_declaration  <br>
 *     ||=  system_timing_check  <br>
 *     ||=  sdpd  
 */
class ScSpecify_item extends ScVerilog {
    ScVerilog item = null;
    public ScSpecify_item(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECIFY_ITEM);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            switch(c.getId())
            {
            case ASTSPECPARAM_DECLARATION:
                item = new ScSpecparam_declaration(c);
                break;
            case ASTPATH_DECLARATION:
                item = new ScPath_declaration(c);
                break;
            case ASTLEVEL_SENSITIVE_PATH_DECLARATION:
                item = new ScLevel_sensitive_path_declaration(c);
                break;
            case ASTEDGE_SENSITIVE_PATH_DECLARATION:
                item = new ScEdge_sensitive_path_declaration(c);
                break;
            case ASTSYSTEM_TIMING_CHECK:
                item = new ScSystem_timing_check(c);
                break;
            case ASTSDPD:
                item = new ScSdpd(c);
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
