package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> process_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | subprogram_body
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | variable_declaration
 *   <br> | file_declaration
 *   <br> | alias_declaration
 *   <br> | attribute_declaration
 *   <br> | attribute_specification
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 */
class ScProcess_declarative_item extends ScVhdl {
    ScVhdl item = null;
    public ScProcess_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTPROCESS_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            item = new ScSubprogram_declaration(node);
            break;
        case ASTSUBPROGRAM_BODY:
            item = new ScSubprogram_body(node);
            break;
        case ASTTYPE_DECLARATION:
            item = new ScType_declaration(node);
            break;
        case ASTSUBTYPE_DECLARATION:
            item = new ScSubtype_declaration(node);
            break;
        case ASTCONSTANT_DECLARATION:
            item = new ScConstant_declaration(node);
            break;
        case ASTVARIABLE_DECLARATION:
            item = new ScVariable_declaration(node);
            break;
        case ASTFILE_DECLARATION:
            item = new ScFile_declaration(node);
            break;
        case ASTALIAS_DECLARATION:
            item = new ScAlias_declaration(node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            item = new ScAttribute_declaration(node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new ScAttribute_specification(node);
            break;
        case ASTUSE_CLAUSE:
            item = new ScUse_clause(node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            item = new ScGroup_template_declaration(node);
            break;
        case ASTGROUP_DECLARATION:
            item = new ScGroup_declaration(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.toString();
    }
}
