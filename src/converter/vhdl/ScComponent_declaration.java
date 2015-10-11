package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> component_declaration ::=
 *   <dd> <b>component</b> identifier [ <b>is</b> ]
 *   <ul> [ <i>local_</i>generic_clause ]
 *   <br> [ <i>local_</i>port_clause ]
 *   </ul> <b>end</b> <b>component</b> [ <i>component_</i>simple_name ] ;
 */
class ScComponent_declaration extends ScVhdl {
    ScIdentifier identifier = null;
    ScGeneric_clause generic = null;
    ScPort_clause port = null;
    public ScComponent_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCOMPONENT_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTGENERIC_CLAUSE:
                generic = new ScGeneric_clause(c);
                break;
            case ASTPORT_CLAUSE:
                port = new ScPort_clause(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";        
        if(generic != null) {
            
            ret += intent() + "template<" + System.lineSeparator();
            startIntentBlock();
            ret += generic.toString();
            endIntentBlock();
            ret += System.lineSeparator() + intent() + ">" + System.lineSeparator();
        }
        
        ret += addLFIntent("SC_MODULE(" + identifier + ")");
        ret += addLFIntent("{");
        startIntentBlock();
        if(port != null) {
            ret += addLF(port.toString());
        }
        endIntentBlock();
        ret += addLFIntent("};");
        return ret;
    }
}
