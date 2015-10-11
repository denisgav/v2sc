package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> report_statement ::=
 *   <dd>  [ label : ]
 *   <ul> <b>report</b> expression
 *   <ul> [ <b>severity</b> expression ] ; </ul></ul>
 */
class ScReport_statement extends ScVhdl {
    ScVhdl report_exp = null;
    ScVhdl severity_exp = null;
    public ScReport_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTREPORT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTVOID:
                {
                    String image = c.firstTokenImage();
                    i++;
                    c = (ASTNode)node.getChild(i);
                    if(image.equalsIgnoreCase(tokenImage[REPORT])) {
                        report_exp = new ScExpression(c);
                    }else if(image.equalsIgnoreCase(tokenImage[SEVERITY])) {
                        severity_exp = new ScExpression(c);
                    }
                }
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        warning("report statement ignored");
        return ret;
    }
}
