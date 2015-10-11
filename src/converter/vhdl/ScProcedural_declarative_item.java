package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> procedural_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | subprogram_body
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | variable_declaration
 *   <br> | alias_declaration
 *   <br> | attribute_declaration
 *   <br> | attribute_specification
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 */
class ScProcedural_declarative_item extends ScVhdl {
    ScVhdl item = null;
    public ScProcedural_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTPROCEDURAL_DECLARATIVE_ITEM);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBPROGRAM_DECLARATION:
                item = new ScSubprogram_declaration(c);
                break;
            case ASTSUBPROGRAM_BODY:
                item = new ScSubprogram_body(c);
                break;
            case ASTTYPE_DECLARATION:
                item = new ScType_declaration(c);
                break;
            case ASTSUBTYPE_DECLARATION:
                item = new ScSubtype_declaration(c);
                break;
            case ASTCONSTANT_DECLARATION:
                item = new ScConstant_declaration(c);
                break;
            case ASTVARIABLE_DECLARATION:
                item = new ScVariable_declaration(c);
                break;
            case ASTALIAS_DECLARATION:
                item = new ScAlias_declaration(c);
                break;
            case ASTATTRIBUTE_DECLARATION:
                item = new ScAttribute_declaration(c);
                break;
            case ASTATTRIBUTE_SPECIFICATION:
                item = new ScAttribute_specification(c);
                break;
            case ASTUSE_CLAUSE:
                item = new ScUse_clause(c);
                break;
            case ASTGROUP_TEMPLATE_DECLARATION:
                item = new ScGroup_template_declaration(c);
                break;
            case ASTGROUP_DECLARATION: 
                item = new ScGroup_declaration(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.toString();
    }
}
