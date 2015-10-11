package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> type_conversion ::=
 *   <dd> type_mark ( expression )
 */
class ScType_conversion extends ScVhdl {
    ScType_mark type_mark = null;
    ScExpression expression = null;
    public ScType_conversion(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_CONVERSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        String tmp = "";
        
        tmp = expression.scString();

        if(isCommonDeclaration) {
            String[] range = null;
            //TODO: ???
            /*Symbol sym = (Symbol)parser.getSymbol(curNode, tmp);
            if(sym != null) {
                if(sym.range != null) {
                    range = sym.range;
                }else {
                    range = new String[3];
                    range[0] = "0";
                    range[1] = RANGE_TO;
                    try {
                        range[2] = String.format("%d", Integer.parseInt(sym.value)-1);
                    }catch(NumberFormatException e) {
                        range[2] = sym.value + "-1";
                    }
                }
            }else*/ {
                range = new String[3];
                range[0] = "0";
                range[1] = RANGE_TO;
                range[2] = tmp + "-1";
            }
            ret = type_mark.getTypeString(range);
        }else {
            String[] range = getTargetRange();
            if(range != null) {
                if(range[1].equalsIgnoreCase(RANGE_TO)) {
                    String tmpr = range[0];
                    range[0] = range[2];
                    range[2] = tmpr;
                    range[1] = RANGE_DOWNTO;
                }
                
                try {
                    int v1 = Integer.parseInt(range[0]);
                    int v2 = Integer.parseInt(range[2]);
                    range[0] = String.format("%d", v1-v2);
                    range[2] = "0";
                }catch(NumberFormatException e) {
                    range[0] = range[0] + "-" + range[2];
                    range[2] = "0";
                }
            }
            ret = type_mark.getTypeString(range);
            ret = encloseBracket(ret);
            ret += tmp;
        }
        return ret;
    }
}
