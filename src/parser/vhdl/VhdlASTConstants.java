package parser.vhdl;

public interface VhdlASTConstants
{
    static final int ASTVOID = 0;
    static final int ASTABSTRACT_LITERAL = 1;
    static final int ASTACCESS_TYPE_DEFINITION = 2;
    static final int ASTACROSS_ASPECT = 3;
    static final int ASTACTUAL_DESIGNATOR = 4;
    static final int ASTACTUAL_PART = 5;
    static final int ASTAGGREGATE = 6;
    static final int ASTALIAS_DECLARATION = 7;
    static final int ASTALIAS_DESIGNATOR = 8;
    static final int ASTALIAS_INDICATION = 9;
    static final int ASTALLOCATOR = 10;
    static final int ASTARCHITECTURE_BODY = 11;
    static final int ASTARCHITECTURE_DECLARATIVE_PART = 12;
    static final int ASTARCHITECTURE_STATEMENT_PART = 13;
    static final int ASTARRAY_NATURE_DEFINITION = 14;
    static final int ASTARRAY_TYPE_DEFINITION = 15;
    static final int ASTASSERTION = 16;
    static final int ASTASSERTION_STATEMENT = 17;
    static final int ASTASSOCIATION_ELEMENT = 18;
    static final int ASTASSOCIATION_LIST = 19;
    static final int ASTATTRIBUTE_DECLARATION = 20;
    static final int ASTATTRIBUTE_DESIGNATOR = 21;
    static final int ASTATTRIBUTE_NAME = 22;
    static final int ASTATTRIBUTE_SPECIFICATION = 23;
    static final int ASTBINDING_INDICATION = 24;
    static final int ASTBLOCK_CONFIGURATION = 25;
    static final int ASTBLOCK_DECLARATIVE_PART = 26;
    static final int ASTBLOCK_HEADER = 27;
    static final int ASTBLOCK_SPECIFICATION = 28;
    static final int ASTBLOCK_STATEMENT = 29;
    static final int ASTBLOCK_STATEMENT_PART = 30;
    static final int ASTBRANCH_QUANTITY_DECLARATION = 31;
    static final int ASTBREAK_ELEMENT = 32;
    static final int ASTBREAK_LIST = 33;
    static final int ASTBREAK_SELECTOR_CLAUSE = 34;
    static final int ASTBREAK_STATEMENT = 35;
    static final int ASTCASE_STATEMENT = 36;
    static final int ASTCASE_STATEMENT_ALTERNATIVE = 37;
    static final int ASTCHOICE = 38;
    static final int ASTCHOICES = 39;
    static final int ASTCOMPONENT_CONFIGURATION = 40;
    static final int ASTCOMPONENT_DECLARATION = 41;
    static final int ASTCOMPONENT_INSTANTIATION_STATEMENT = 42;
    static final int ASTCOMPONENT_SPECIFICATION = 43;
    static final int ASTCOMPOSITE_NATURE_DEFINITION = 44;
    static final int ASTCOMPOSITE_TYPE_DEFINITION = 45;
    static final int ASTCONCURRENT_ASSERTION_STATEMENT = 46;
    static final int ASTCONCURRENT_BREAK_STATEMENT = 47;
    static final int ASTCONCURRENT_PROCEDURE_CALL_STATEMENT = 48;
    static final int ASTCONCURRENT_SIGNAL_ASSIGNMENT_STATEMENT = 49;
    static final int ASTCONCURRENT_STATEMENT = 50;
    static final int ASTCONDITION_CLAUSE = 51;
    static final int ASTCONDITIONAL_SIGNAL_ASSIGNMENT = 52;
    static final int ASTCONDITIONAL_WAVEFORMS = 53;
    static final int ASTCONFIGURATION_DECLARATION = 54;
    static final int ASTCONFIGURATION_DECLARATIVE_PART = 55;
    static final int ASTCONFIGURATION_ITEM = 56;
    static final int ASTCONFIGURATION_SPECIFICATION = 57;
    static final int ASTCONSTANT_DECLARATION = 58;
    static final int ASTCONSTRAINED_ARRAY_DEFINITION = 59;
    static final int ASTCONSTRAINED_NATURE_DEFINITION = 60;
    static final int ASTCONSTRAINT = 61;
    static final int ASTCONTEXT_CLAUSE = 62;
    static final int ASTDELAY_MECHANISM = 63;
    static final int ASTDESIGN_FILE = 64;
    static final int ASTDESIGN_UNIT = 65;
    static final int ASTDESIGNATOR = 66;
    static final int ASTDIRECTION = 67;
    static final int ASTDISCONNECTION_SPECIFICATION = 68;
    static final int ASTDISCRETE_RANGE = 69;
    static final int ASTELEMENT_ASSOCIATION = 70;
    static final int ASTELEMENT_DECLARATION = 71;
    static final int ASTELEMENT_SUBNATURE_DEFINITION = 72;
    static final int ASTELEMENT_SUBTYPE_DEFINITION = 73;
    static final int ASTENTITY_ASPECT = 74;
    static final int ASTENTITY_CLASS_ENTRY = 75;
    static final int ASTENTITY_CLASS_ENTRY_LIST = 76;
    static final int ASTENTITY_DECLARATION = 77;
    static final int ASTENTITY_DECLARATIVE_PART = 78;
    static final int ASTENTITY_DESIGNATOR = 79;
    static final int ASTENTITY_HEADER = 80;
    static final int ASTENTITY_NAME_LIST = 81;
    static final int ASTENTITY_SPECIFICATION = 82;
    static final int ASTENTITY_STATEMENT_PART = 83;
    static final int ASTENTITY_TAG = 84;
    static final int ASTENUMERATION_LITERAL = 85;
    static final int ASTENUMERATION_TYPE_DEFINITION = 86;
    static final int ASTEXIT_STATEMENT = 87;
    static final int ASTEXPRESSION = 88;
    static final int ASTFACTOR = 89;
    static final int ASTFILE_DECLARATION = 90;
    static final int ASTFILE_LOGICAL_NAME = 91;
    static final int ASTFILE_OPEN_INFORMATION = 92;
    static final int ASTFILE_TYPE_DEFINITION = 93;
    static final int ASTFORMAL_DESIGNATOR = 94;
    static final int ASTFORMAL_PARAMETER_LIST = 95;
    static final int ASTFORMAL_PART = 96;
    static final int ASTFREE_QUANTITY_DECLARATION = 97;
    static final int ASTFULL_TYPE_DECLARATION = 98;
    static final int ASTFUNCTION_CALL = 99;
    static final int ASTGENERATE_STATEMENT = 100;
    static final int ASTGENERATION_SCHEME = 101;
    static final int ASTGENERIC_CLAUSE = 102;
    static final int ASTGENERIC_LIST = 103;
    static final int ASTGENERIC_MAP_ASPECT = 104;
    static final int ASTGROUP_CONSTITUENT = 105;
    static final int ASTGROUP_CONSTITUENT_LIST = 106;
    static final int ASTGROUP_DECLARATION = 107;
    static final int ASTGROUP_TEMPLATE_DECLARATION = 108;
    static final int ASTGUARDED_SIGNAL_SPECIFICATION = 109;
    static final int ASTIDENTIFIER = 110;
    static final int ASTIDENTIFIER_LIST = 111;
    static final int ASTIF_STATEMENT = 112;
    static final int ASTINCOMPLETE_TYPE_DECLARATION = 113;
    static final int ASTINDEX_CONSTRAINT = 114;
    static final int ASTINDEX_SPECIFICATION = 115;
    static final int ASTINDEX_SUBTYPE_DEFINITION = 116;
    static final int ASTINDEXED_NAME = 117;
    static final int ASTINSTANTIATED_UNIT = 118;
    static final int ASTINSTANTIATION_LIST = 119;
    static final int ASTINTEGER_TYPE_DEFINITION = 120;
    static final int ASTINTERFACE_CONSTANT_DECLARATION = 121;
    static final int ASTINTERFACE_DECLARATION = 122;
    static final int ASTINTERFACE_ELEMENT = 123;
    static final int ASTINTERFACE_FILE_DECLARATION = 124;
    static final int ASTINTERFACE_LIST = 125;
    static final int ASTINTERFACE_QUANTITY_DECLARATION = 126;
    static final int ASTINTERFACE_SIGNAL_DECLARATION = 127;
    static final int ASTINTERFACE_TERMINAL_DECLARATION = 128;
    static final int ASTINTERFACE_VARIABLE_DECLARATION = 129;
    static final int ASTITERATION_SCHEME = 130;
    static final int ASTLIBRARY_CLAUSE = 131;
    static final int ASTLIBRARY_UNIT = 132;
    static final int ASTLITERAL = 133;
    static final int ASTLOGICAL_NAME_LIST = 134;
    static final int ASTLOOP_STATEMENT = 135;
    static final int ASTMODE = 136;
    static final int ASTNAME = 137;
    static final int ASTNATURE_DECLARATION = 138;
    static final int ASTNATURE_DEFINITION = 139;
    static final int ASTNATURE_ELEMENT_DECLARATION = 140;
    static final int ASTNATURE_MARK = 141;
    static final int ASTNEXT_STATEMENT = 142;
    static final int ASTNULL_STATEMENT = 143;
    static final int ASTNUMERIC_LITERAL = 144;
    static final int ASTOPERATOR_SYMBOL = 145;
    static final int ASTOPTIONS = 146;
    static final int ASTPACKAGE_BODY = 147;
    static final int ASTPACKAGE_BODY_DECLARATIVE_PART = 148;
    static final int ASTPACKAGE_DECLARATION = 149;
    static final int ASTPACKAGE_DECLARATIVE_PART = 150;
    static final int ASTPARAMETER_SPECIFICATION = 151;
    static final int ASTPHYSICAL_LITERAL = 152;
    static final int ASTPHYSICAL_TYPE_DEFINITION = 153;
    static final int ASTPORT_CLAUSE = 154;
    static final int ASTPORT_LIST = 155;
    static final int ASTPORT_MAP_ASPECT = 156;
    static final int ASTPREFIX = 157;
    static final int ASTPRIMARY = 158;
    static final int ASTPRIMARY_UNIT = 159;
    static final int ASTPRIMARY_UNIT_DECLARATION = 160;
    static final int ASTPROCEDURAL_DECLARATIVE_PART = 161;
    static final int ASTPROCEDURAL_STATEMENT_PART = 162;
    static final int ASTPROCEDURE_CALL = 163;
    static final int ASTPROCEDURE_CALL_STATEMENT = 164;
    static final int ASTPROCESS_DECLARATIVE_PART = 165;
    static final int ASTPROCESS_STATEMENT = 166;
    static final int ASTPROCESS_STATEMENT_PART = 167;
    static final int ASTQUALIFIED_EXPRESSION = 168;
    static final int ASTQUANTITY_DECLARATION = 169;
    static final int ASTQUANTITY_LIST = 170;
    static final int ASTQUANTITY_SPECIFICATION = 171;
    static final int ASTRANGE = 172;
    static final int ASTRANGE_CONSTRAINT = 173;
    static final int ASTRECORD_NATURE_DEFINITION = 174;
    static final int ASTRECORD_TYPE_DEFINITION = 175;
    static final int ASTRELATION = 176;
    static final int ASTREPORT_STATEMENT = 177;
    static final int ASTRETURN_STATEMENT = 178;
    static final int ASTSCALAR_NATURE_DEFINITION = 179;
    static final int ASTSCALAR_TYPE_DEFINITION = 180;
    static final int ASTSECONDARY_UNIT = 181;
    static final int ASTSECONDARY_UNIT_DECLARATION = 182;
    static final int ASTSELECTED_NAME = 183;
    static final int ASTSELECTED_SIGNAL_ASSIGNMENT = 184;
    static final int ASTSELECTED_WAVEFORMS = 185;
    static final int ASTSENSITIVITY_CLAUSE = 186;
    static final int ASTSENSITIVITY_LIST = 187;
    static final int ASTSEQUENCE_OF_STATEMENTS = 188;
    static final int ASTSHIFT_EXPRESSION = 189;
    static final int ASTSIGN = 190;
    static final int ASTSIGNAL_ASSIGNMENT_STATEMENT = 191;
    static final int ASTSIGNAL_DECLARATION = 192;
    static final int ASTSIGNAL_KIND = 193;
    static final int ASTSIGNAL_LIST = 194;
    static final int ASTSIGNATURE = 195;
    static final int ASTSIMPLE_EXPRESSION = 196;
    static final int ASTSIMPLE_SIMULTANEOUS_STATEMENT = 197;
    static final int ASTSIMULTANEOUS_ALTERNATIVE = 198;
    static final int ASTSIMULTANEOUS_CASE_STATEMENT = 199;
    static final int ASTSIMULTANEOUS_IF_STATEMENT = 200;
    static final int ASTSIMULTANEOUS_NULL_STATEMENT = 201;
    static final int ASTSIMULTANEOUS_PROCEDURAL_STATEMENT = 202;
    static final int ASTSIMULTANEOUS_STATEMENT = 203;
    static final int ASTSIMULTANEOUS_STATEMENT_PART = 204;
    static final int ASTSLICE_NAME = 205;
    static final int ASTSOURCE_ASPECT = 206;
    static final int ASTSOURCE_QUANTITY_DECLARATION = 207;
    static final int ASTSTEP_LIMIT_SPECIFICATION = 208;
    static final int ASTSUBNATURE_DECLARATION = 209;
    static final int ASTSUBNATURE_INDICATION = 210;
    static final int ASTSUBPROGRAM_BODY = 211;
    static final int ASTSUBPROGRAM_DECLARATION = 212;
    static final int ASTSUBPROGRAM_DECLARATIVE_PART = 213;
    static final int ASTSUBPROGRAM_SPECIFICATION = 214;
    static final int ASTSUBPROGRAM_STATEMENT_PART = 215;
    static final int ASTSUBTYPE_DECLARATION = 216;
    static final int ASTSUBTYPE_INDICATION = 217;
    static final int ASTSUFFIX = 218;
    static final int ASTTARGET = 219;
    static final int ASTTERM = 220;
    static final int ASTTERMINAL_ASPECT = 221;
    static final int ASTTERMINAL_DECLARATION = 222;
    static final int ASTTHROUGH_ASPECT = 223;
    static final int ASTTIMEOUT_CLAUSE = 224;
    static final int ASTTOLERANCE_ASPECT = 225;
    static final int ASTTYPE_CONVERSION = 226;
    static final int ASTTYPE_DECLARATION = 227;
    static final int ASTTYPE_DEFINITION = 228;
    static final int ASTTYPE_MARK = 229;
    static final int ASTUNCONSTRAINED_ARRAY_DEFINITION = 230;
    static final int ASTUNCONSTRAINED_NATURE_DEFINITION = 231;
    static final int ASTUSE_CLAUSE = 232;
    static final int ASTVARIABLE_ASSIGNMENT_STATEMENT = 233;
    static final int ASTVARIABLE_DECLARATION = 234;
    static final int ASTWAIT_STATEMENT = 235;
    static final int ASTWAVEFORM = 236;
    static final int ASTWAVEFORM_ELEMENT = 237;

    static final String[] ASTNodeName = 
    {
        "void",
        "abstract_literal",
        "access_type_definition",
        "across_aspect",
        "actual_designator",
        "actual_part",
        "aggregate",
        "alias_declaration",
        "alias_designator",
        "alias_indication",
        "allocator",
        "architecture_body",
        "architecture_declarative_part",
        "architecture_statement_part",
        "array_nature_definition",
        "array_type_definition",
        "assertion",
        "assertion_statement",
        "association_element",
        "association_list",
        "attribute_declaration",
        "attribute_designator",
        "attribute_name",
        "attribute_specification",
        "binding_indication",
        "block_configuration",
        "block_declarative_part",
        "block_header",
        "block_specification",
        "block_statement",
        "block_statement_part",
        "branch_quantity_declaration",
        "break_element",
        "break_list",
        "break_selector_clause",
        "break_statement",
        "case_statement",
        "case_statement_alternative",
        "choice",
        "choices",
        "component_configuration",
        "component_declaration",
        "component_instantiation_statement",
        "component_specification",
        "composite_nature_definition",
        "composite_type_definition",
        "concurrent_assertion_statement",
        "concurrent_break_statement",
        "concurrent_procedure_call_statement",
        "concurrent_signal_assignment_statement",
        "concurrent_statement",
        "condition_clause",
        "conditional_signal_assignment",
        "conditional_waveforms",
        "configuration_declaration",
        "configuration_declarative_part",
        "configuration_item",
        "configuration_specification",
        "constant_declaration",
        "constrained_array_definition",
        "constrained_nature_definition",
        "constraint",
        "context_clause",
        "delay_mechanism",
        "design_file",
        "design_unit",
        "designator",
        "direction",
        "disconnection_specification",
        "discrete_range",
        "element_association",
        "element_declaration",
        "element_subnature_definition",
        "element_subtype_definition",
        "entity_aspect",
        "entity_class_entry",
        "entity_class_entry_list",
        "entity_declaration",
        "entity_declarative_part",
        "entity_designator",
        "entity_header",
        "entity_name_list",
        "entity_specification",
        "entity_statement_part",
        "entity_tag",
        "enumeration_literal",
        "enumeration_type_definition",
        "exit_statement",
        "expression",
        "factor",
        "file_declaration",
        "file_logical_name",
        "file_open_information",
        "file_type_definition",
        "formal_designator",
        "formal_parameter_list",
        "formal_part",
        "free_quantity_declaration",
        "full_type_declaration",
        "function_call",
        "generate_statement",
        "generation_scheme",
        "generic_clause",
        "generic_list",
        "generic_map_aspect",
        "group_constituent",
        "group_constituent_list",
        "group_declaration",
        "group_template_declaration",
        "guarded_signal_specification",
        "identifier",
        "identifier_list",
        "if_statement",
        "incomplete_type_declaration",
        "index_constraint",
        "index_specification",
        "index_subtype_definition",
        "indexed_name",
        "instantiated_unit",
        "instantiation_list",
        "integer_type_definition",
        "interface_constant_declaration",
        "interface_declaration",
        "interface_element",
        "interface_file_declaration",
        "interface_list",
        "interface_quantity_declaration",
        "interface_signal_declaration",
        "interface_terminal_declaration",
        "interface_variable_declaration",
        "iteration_scheme",
        "library_clause",
        "library_unit",
        "literal",
        "logical_name_list",
        "loop_statement",
        "mode",
        "name",
        "nature_declaration",
        "nature_definition",
        "nature_element_declaration",
        "nature_mark",
        "next_statement",
        "null_statement",
        "numeric_literal",
        "operator_symbol",
        "options",
        "package_body",
        "package_body_declarative_part",
        "package_declaration",
        "package_declarative_part",
        "parameter_specification",
        "physical_literal",
        "physical_type_definition",
        "port_clause",
        "port_list",
        "port_map_aspect",
        "prefix",
        "primary",
        "primary_unit",
        "primary_unit_declaration",
        "procedural_declarative_part",
        "procedural_statement_part",
        "procedure_call",
        "procedure_call_statement",
        "process_declarative_part",
        "process_statement",
        "process_statement_part",
        "qualified_expression",
        "quantity_declaration",
        "quantity_list",
        "quantity_specification",
        "range",
        "range_constraint",
        "record_nature_definition",
        "record_type_definition",
        "relation",
        "report_statement",
        "return_statement",
        "scalar_nature_definition",
        "scalar_type_definition",
        "secondary_unit",
        "secondary_unit_declaration",
        "selected_name",
        "selected_signal_assignment",
        "selected_waveforms",
        "sensitivity_clause",
        "sensitivity_list",
        "sequence_of_statements",
        "shift_expression",
        "sign",
        "signal_assignment_statement",
        "signal_declaration",
        "signal_kind",
        "signal_list",
        "signature",
        "simple_expression",
        "simple_simultaneous_statement",
        "simultaneous_alternative",
        "simultaneous_case_statement",
        "simultaneous_if_statement",
        "simultaneous_null_statement",
        "simultaneous_procedural_statement",
        "simultaneous_statement",
        "simultaneous_statement_part",
        "slice_name",
        "source_aspect",
        "source_quantity_declaration",
        "step_limit_specification",
        "subnature_declaration",
        "subnature_indication",
        "subprogram_body",
        "subprogram_declaration",
        "subprogram_declarative_part",
        "subprogram_specification",
        "subprogram_statement_part",
        "subtype_declaration",
        "subtype_indication",
        "suffix",
        "target",
        "term",
        "terminal_aspect",
        "terminal_declaration",
        "through_aspect",
        "timeout_clause",
        "tolerance_aspect",
        "type_conversion",
        "type_declaration",
        "type_definition",
        "type_mark",
        "unconstrained_array_definition",
        "unconstrained_nature_definition",
        "use_clause",
        "variable_assignment_statement",
        "variable_declaration",
        "wait_statement",
        "waveform",
        "waveform_element",
    };
}
