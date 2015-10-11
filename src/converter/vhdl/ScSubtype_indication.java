package converter.vhdl;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


/**
 * <dl> subtype_indication ::=
 *   <dd> [ <i>resolution_function_</i>name ] type_mark [ constraint ] [ tolerance_aspect ]
 */
class ScSubtype_indication extends ScVhdl {
    ScName name = null;
    ScType_mark type_mark = null;
    ScConstraint constraint = null;
    ScTolerance_aspect tolerance = null;
    public ScSubtype_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBTYPE_INDICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            case ASTCONSTRAINT:
                constraint = new ScConstraint(c);
                break;
            case ASTTOLERANCE_ASPECT:
                tolerance = new ScTolerance_aspect(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        return type_mark.getBitWidth();
    }
    
    public String getMin() {
        String ret = "0";
        if(constraint != null){
            ret = constraint.getMin();
        }else {
            //TODO: get subtype range
            Symbol sym = (Symbol)parser.getSymbol(curNode, type_mark.scString());
            if(sym != null && sym.typeRange != null) {
                String[] range = sym.typeRange;
                if(range[1].equalsIgnoreCase(RANGE_DOWNTO)) {
                    ret = range[2];
                }
            }
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(constraint != null){
            ret = constraint.getMax();
        }else {
          //TODO: get subtype range
            Symbol sym = (Symbol)parser.getSymbol(curNode, type_mark.scString());
            if(sym != null && sym.typeRange != null) {
                String[] range = sym.typeRange;
                if(range[1].equalsIgnoreCase(RANGE_DOWNTO)) {
                    ret = range[0];
                }
            }
        }
        return ret;
    }
    
    public boolean isDownto() {
        boolean ret = false;
        if(constraint != null){
            ret = constraint.isDownto();
        }else {
            Symbol sym = (Symbol)parser.getSymbol(curNode, type_mark.scString());
            if(sym != null && sym.typeRange != null) {
                String[] range = sym.typeRange;
                ret = range[1].equalsIgnoreCase(RANGE_DOWNTO);
            }
        }
        return ret;
    }
    
    public String[] getRange() {
        String[] ret = null;
        if(constraint != null){
            ret = constraint.getRange();
        }else {
            //TODO: get subtype range
            Symbol sym = (Symbol)parser.getSymbol(curNode, type_mark.scString());
            if(sym != null) {
                ret = sym.typeRange;
            }
        }
        return ret;
    }
    
    public String[] getValueRange() {
        if(constraint != null) {
            return constraint.getValueRange();
        }else {
            return null;
        }
    }
    
    public String subTypeString(String type) {
        String ret = "";
        if(curNode.isDescendantOf(ASTSUBTYPE_DECLARATION)) {
            ret += getReplaceType(type);
            if(tolerance != null) {
                warning("tolerance ignored");
            }
        }else {
            ret = scString();
        }
        return ret;
    }
    
    public String scString() {
        String ret = "";
        if(constraint != null) {
            String[] trange = constraint.getTypeRange();
            String[] vrange = constraint.getValueRange();
            
            if(vrange != null && curNode.isDescendantOf(ASTSUBTYPE_DECLARATION)) {
                ret += "/* ";
                ret += vrange[0] + " " + vrange[1] + " " + vrange[2];
                ret += " */";
            }
            //warning("constraint ignored");
            ret += type_mark.getTypeString(trange);
        }else {
            ret += type_mark.scString();
        }
        if(tolerance != null) {
            warning("tolerance ignored");
        }
        return ret;
    }
}
