package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> attribute_name ::=
 *   <dd> prefix [ signature ] ' attribute_designator [ ( expression { , expression } ) ]
 */
class ScAttribute_name extends ScVhdl {
    ScVhdl prefix = null;
    ScVhdl signature = null;
    ScVhdl designator = null;
    ArrayList<ScVhdl> expressions = new ArrayList<ScVhdl>();
    public ScAttribute_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTATTRIBUTE_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new ScPrefix(c);
                break;
            case ASTSIGNATURE:
                signature = new ScSignature(c);
                break;
            case ASTATTRIBUTE_DESIGNATOR:
                designator = new ScAttribute_designator(c);
                break;
            case ASTEXPRESSION:
                {
                    ScVhdl exp  = new ScExpression(c);
                    expressions.add(exp);
                }
                break;
            default:
                break;
            }
        }
    }
    
    public String getMin() {
        String[] names = {prefix.curNode.firstTokenImage()};
        String[] range = getTypeRange(curNode, names);
        if(range == null) {
            range = getArrayRange(curNode, names);
        }
        
        if(range == null) {
            return "0";
        }
        
        if(range[1].equalsIgnoreCase(RANGE_TO)) {
            return range[2];
        }else {
            return range[0];
        }
    }
    
    public String getMax() {
        String[] names = {prefix.curNode.firstTokenImage()};
        String[] range = getTypeRange(curNode, names);
        if(range == null) {
            range = getArrayRange(curNode, names);
        }
        
        if(range == null) {
            return names[0] + ".length() - 1";
        }
        
        if(range[1].equalsIgnoreCase(RANGE_TO)) {
            return range[0];
        }else {
            return range[2];
        }
    }
    
    public String[] getRange() {
        String[] names = {prefix.curNode.firstTokenImage()};
        String[] range = getTypeRange(curNode, names);
        if(range == null) {
            range = getArrayRange(curNode, names);
        }
        return range;
    }

    public String scString() {
        String ret = "";
        if(signature != null) {
            warning("signature ignored");
        }
        
        String attri = designator.scString();
        //if(attri.equalsIgnoreCase("left")) {
        //    ret += getMax();
        //}else if(attri.equalsIgnoreCase("right")) {
        //    ret += getMin();
        //}else {
            ret += prefix.scString();
            ret += "." + attri;
            ret += "(";
            if(expressions.size() > 0) {
                for(int i = 0; i < expressions.size(); i++) {
                    ret += expressions.get(i).scString();
                    if(i < expressions.size() - 1) {
                        ret += ", ";
                    }
                }
            }
            ret += ")";
        //}
        return ret;
    }
}
