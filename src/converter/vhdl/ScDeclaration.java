package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> declaration ::=
 *   <dd> type_declaration
 *   <br> | subtype_declaration
 *   <br> | object_declaration
 *   <br> | interface_declaration
 *   <br> | alias_declaration
 *   <br> | attribute_declaration
 *   <br> | component_declaration
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 *   <br> | entity_declaration
 *   <br> | configuration_declaration
 *   <br> | subprogram_declaration
 *   <br> | package_declaration
 *   <br> | nature_declaration
 *   <br> | subnature_declaration
 *   <br> | quantity_declaration
 *   <br> | terminal_declaration
 */
class ScDeclaration extends ScVhdl {
    public ScDeclaration(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTDECLARATION);
        // no use by others module
    }

    public String scString() {
        return "";
    }
}
