package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> assertion ::=
 *   <dd> <b>assert</b> condition
 *   <ul> [ <b>report</b> expression ]
 *   <br> [ <b>severity</b> expression ] </ul>
 */
class ScAssertion extends ScVhdl {
    ScVhdl condition = null;
    ScVhdl report_exp = null;
    ScVhdl severity_exp = null;
    public ScAssertion(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSERTION);
        for(int i = 0; i < node.getChildrenNum(); i += 2) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTVOID);  // the first must be token
            String image = c.firstTokenImage(); 
            c = (ASTNode)node.getChild(i+1);
            if(image.equalsIgnoreCase(tokenImage[ASSERT])) {
                condition = new ScCondition(c);
            }else if(image.equalsIgnoreCase(tokenImage[REPORT])) {
                report_exp = new ScExpression(c);
            }else if(image.equalsIgnoreCase(tokenImage[SEVERITY])) {
                severity_exp = new ScExpression(c);
            }
        }
    }

    public String scString() {
        String ret = "";
        if(report_exp != null) {
            ret += "if(!(" + condition.scString() + "))" + System.lineSeparator();
            ret += intent(curLevel+1) + "printf(";
            ret += report_exp.scString() + ");" + System.lineSeparator();
        }
        ret += intent() + "assert(" + condition.scString() + ")";
        if(severity_exp != null) {
            warning("assertion severity ignored");
        }
        return ret;
    }
}
