package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> based_literal ::=
 *   <dd> base # based_integer [ . based_integer ] # [ exponent ]
 */
class ScBased_literal extends ScVhdl {
    String base = "10";
    String based_integer = "0";
    String fract_based_integer = "";
    String exponent = "";
    public ScBased_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTBASED_LITERAL);
        String image = node.firstTokenImage();
        int index = image.indexOf('#');
        base = getBase(image.substring(0, index));
        int index1 = image.indexOf('.', index+1);
        int index2 = image.indexOf('#', index+1);
        if(index1 > 0) {
            based_integer = getBased_integer(image.substring(index+1, index1));
            if(index2 > 0) {
                fract_based_integer = getBased_integer(image.substring(index1+1, index2));
            }else {
                fract_based_integer = getBased_integer(image.substring(index1+1));
            }
        }else if(index2 > 0) {
            based_integer = getBased_integer(image.substring(index+1, index2));
            if(index2 < image.length() - 1)
                exponent = getExponent(image.substring(index2+1));
        }
    }

    public String scString() {
        String ret = "";
        int radix = Integer.parseInt(base);
        if(radix == 16) {
            ret += "0x";
            ret += based_integer;
            if(!fract_based_integer.isEmpty()) {
                ret += "." + fract_based_integer;
            }
        }else {
            ret += Integer.parseInt(based_integer, radix);
            if(!fract_based_integer.isEmpty()) {
                ret += "." + Integer.parseInt(fract_based_integer, radix);
            }
        }
        ret += exponent;

        return ret;
    }
}
