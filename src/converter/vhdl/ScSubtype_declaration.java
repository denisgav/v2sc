package converter.vhdl;

import common.MyDebug;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


/**
 * <dl> subtype_declaration ::=
 *   <dd> <b>subtype</b> identifier <b>is</b> subtype_indication ;
 */
class ScSubtype_declaration extends ScVhdl {
    ScIdentifier identifier = null;
    ScSubtype_indication subtype = null;
    public ScSubtype_declaration(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTSUBTYPE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTSUBTYPE_INDICATION:
                subtype = new ScSubtype_indication(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent();
        ret += "typedef";
        
        String strType = subtype.type_mark.name.scString();
        Symbol sym = (Symbol)parser.getSymbol(curNode, strType);
        if(sym == null) {
            MyDebug.printFileLine("subtype no correspond type!");
            ret += " " + subtype.scString();
            ret += " " + identifier.scString() + ";";
            return ret;
        }
        
        String fullType = sym.type;
        ret += " " + subtype.subTypeString(fullType);
        ret += " " + identifier;
        
        if(subtype.constraint != null && subtype.constraint.index != null) {
            ret += encloseBracket(addOne(subtype.constraint.index.getMax()), "[]");
        }
        ret += ";";
        return ret;
    }
}
