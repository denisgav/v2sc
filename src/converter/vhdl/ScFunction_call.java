package converter.vhdl;

import common.MyDebug;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


/**
 * <dl> function_call ::=
 *   <dd> <i>function_</i>name [ ( actual_parameter_part ) ]
 */
class ScFunction_call extends ScVhdl {
    ScName name = null;
    ScActual_parameter_part param_part= null;
    public ScFunction_call(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFUNCTION_CALL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTASSOCIATION_LIST:
                param_part = new ScActual_parameter_part(c);
                break;
            default:
                break;
            }
        }
    }
    
    public int getBitWidth() {
        Symbol sym = (Symbol)parser.getSymbol(curNode, name.getNameSegments());
        if(sym == null || sym.typeRange == null) {
            MyDebug.printFileLine("type range of " + name + " is null");
            return 1;
        }
        return getWidth(sym.typeRange[0], sym.typeRange[2]);
    }

    public String scString() {
        String ret = "";
        String strName = name.scString();
        if(param_part != null) {
            String tmp = param_part.scString();
            if(strName.equalsIgnoreCase("rising_edge")) {
                ret += tmp + ".posedge()";
            }else if(strName.equalsIgnoreCase("falling_edge")) {
                ret += tmp + ".negedge()";
            }else if(strName.equalsIgnoreCase("conv_integer")) {
                ret += tmp + ".to_int()";
            }else {
                ret += strName + encloseBracket(tmp);
            }
        }else {
            ret += strName + "()";
        }
        return ret;
    }
}
