package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> bit_string_literal ::=
 *   <dd> base_specifier " [ bit_value ] "
 */
class ScBit_string_literal extends ScVhdl {
    int base = 2;
    String bit_value = "";
    public ScBit_string_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTBIT_STRING_LITERAL);
        String image = node.firstTokenImage();
        base = getBase_specifier(image.substring(0, 1));
        int index = image.indexOf('\"');
        int index1 = image.lastIndexOf('\"');
        bit_value = image.substring(index+1, index1).trim();
    }

    public String scString() {
        String ret = "";
        int value = 0;
        switch(base)
        {
        case 2:
            ret = bit_value;
        case 16:
            ret = "0x" + bit_value;
            break;
        case 8:
        default:
            value = Integer.parseInt(bit_value, base);
            ret = String.format("0x%x", value);
            break;
        }
        return ret;
    }
}
