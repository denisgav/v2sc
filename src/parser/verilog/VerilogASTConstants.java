package parser.verilog;

public interface VerilogASTConstants
{
    static final int ASTALWAYS_STATEMENT = 0;
    static final int ASTASSIGNMENT = 1;
    static final int ASTBASE = 2;
    static final int ASTBINARY_OPERATOR = 3;
    static final int ASTBLOCK_DECLARATION = 4;
    static final int ASTBLOCKING_ASSIGNMENT = 5;
    static final int ASTCASE_ITEM = 6;
    static final int ASTCHARGE_STRENGTH = 7;
    static final int ASTCOMBINATIONAL_ENTRY = 8;
    static final int ASTCONCATENATION = 9;
    static final int ASTCONDITIONAL_PORT_EXPRESSION = 10;
    static final int ASTCONSTANT_EXPRESSION = 11;
    static final int ASTCONTINUOUS_ASSIGN = 12;
    static final int ASTCONTROLLED_TIMING_CHECK_EVENT = 13;
    static final int ASTDATA_SOURCE_EXPRESSION = 14;
    static final int ASTDECIMAL_NUMBER = 15;
    static final int ASTDELAY = 16;
    static final int ASTDELAY_CONTROL = 17;
    static final int ASTDELAY_OR_EVENT_CONTROL = 18;
    static final int ASTDESCRIPTION = 19;
    static final int ASTDRIVE_STRENGTH = 20;
    static final int ASTEDGE = 21;
    static final int ASTEDGE_CONTROL_SPECIFIER = 22;
    static final int ASTEDGE_DESCRIPTOR = 23;
    static final int ASTEDGE_IDENTIFIER = 24;
    static final int ASTEDGE_INPUT_LIST = 25;
    static final int ASTEDGE_SENSITIVE_PATH_DECLARATION = 26;
    static final int ASTEDGE_SYMBOL = 27;
    static final int ASTEVENT_CONTROL = 28;
    static final int ASTEVENT_DECLARATION = 29;
    static final int ASTEVENT_EXPRESSION = 30;
    static final int ASTEXPANDRANGE = 31;
    static final int ASTEXPRESSION = 32;
    static final int ASTFUNCTION = 33;
    static final int ASTFUNCTION_CALL = 34;
    static final int ASTGATE_DECLARATION = 35;
    static final int ASTGATE_INSTANCE = 36;
    static final int ASTGATETYPE = 37;
    static final int ASTidentifier = 38;
    static final int ASTIDENTIFIER = 39;
    static final int ASTINIT_VAL = 40;
    static final int ASTINITIAL_STATEMENT = 41;
    static final int ASTINOUT_DECLARATION = 42;
    static final int ASTINPUT_DECLARATION = 43;
    static final int ASTINPUT_IDENTIFIER = 44;
    static final int ASTINPUT_LIST = 45;
    static final int ASTINTEGER_DECLARATION = 46;
    static final int ASTLEVEL_INPUT_LIST = 47;
    static final int ASTLEVEL_SENSITIVE_PATH_DECLARATION = 48;
    static final int ASTLEVEL_SYMBOL = 49;
    static final int ASTLIST_OF_ASSIGNMENTS = 50;
    static final int ASTLIST_OF_MODULE_CONNECTIONS = 51;
    static final int ASTLIST_OF_PARAM_ASSIGNMENTS = 52;
    static final int ASTLIST_OF_PATH_INPUTS = 53;
    static final int ASTLIST_OF_PATH_OUTPUTS = 54;
    static final int ASTLIST_OF_PORTS = 55;
    static final int ASTLIST_OF_REGISTER_VARIABLES = 56;
    static final int ASTLIST_OF_VARIABLES = 57;
    static final int ASTLVALUE = 58;
    static final int ASTMINTYPMAX_EXPRESSION = 59;
    static final int ASTMODULE = 60;
    static final int ASTMODULE_INSTANCE = 61;
    static final int ASTMODULE_INSTANTIATION = 62;
    static final int ASTMODULE_ITEM = 63;
    static final int ASTMODULE_PORT_CONNECTION = 64;
    static final int ASTMULTIPLE_CONCATENATION = 65;
    static final int ASTNAME_OF_BLOCK = 66;
    static final int ASTNAME_OF_EVENT = 67;
    static final int ASTNAME_OF_FUNCTION = 68;
    static final int ASTNAME_OF_GATE_INSTANCE = 69;
    static final int ASTNAME_OF_INSTANCE = 70;
    static final int ASTNAME_OF_MEMORY = 71;
    static final int ASTNAME_OF_MODULE = 72;
    static final int ASTNAME_OF_PORT = 73;
    static final int ASTNAME_OF_REGISTER = 74;
    static final int ASTNAME_OF_SYSTEM_FUNCTION = 75;
    static final int ASTNAME_OF_SYSTEM_TASK = 76;
    static final int ASTNAME_OF_TASK = 77;
    static final int ASTNAME_OF_UDP = 78;
    static final int ASTNAME_OF_UDP_INSTANCE = 79;
    static final int ASTNAME_OF_VARIABLE = 80;
    static final int ASTNAMED_PORT_CONNECTION = 81;
    static final int ASTNET_DECLARATION = 82;
    static final int ASTNETTYPE = 83;
    static final int ASTNEXT_STATE = 84;
    static final int ASTNON_BLOCKING_ASSIGNMENT = 85;
    static final int ASTNOTIFY_REGISTER = 86;
    static final int ASTNULL = 87;
    static final int ASTNUMBER = 88;
    static final int ASTOUTPUT_DECLARATION = 89;
    static final int ASTOUTPUT_IDENTIFIER = 90;
    static final int ASTOUTPUT_SYMBOL = 91;
    static final int ASTOUTPUT_TERMINAL_NAME = 92;
    static final int ASTPAR_BLOCK = 93;
    static final int ASTPARAM_ASSIGNMENT = 94;
    static final int ASTPARAMETER_DECLARATION = 95;
    static final int ASTPARAMETER_OVERRIDE = 96;
    static final int ASTPARAMETER_VALUE_ASSIGNMENT = 97;
    static final int ASTPATH_DECLARATION = 98;
    static final int ASTPATH_DELAY_EXPRESSION = 99;
    static final int ASTPATH_DELAY_VALUE = 100;
    static final int ASTPATH_DESCRIPTION = 101;
    static final int ASTPOLARITY_OPERATOR = 102;
    static final int ASTPORT = 103;
    static final int ASTPORT_EXPRESSION = 104;
    static final int ASTPORT_REFERENCE = 105;
    static final int ASTPRIMARY = 106;
    static final int ASTRANGE = 107;
    static final int ASTRANGE_OR_TYPE = 108;
    static final int ASTREAL_DECLARATION = 109;
    static final int ASTREG_DECLARATION = 110;
    static final int ASTREGISTER_VARIABLE = 111;
    static final int ASTSCALAR_CONSTANT = 112;
    static final int ASTSCALAR_EVENT_EXPRESSION = 113;
    static final int ASTSCALAR_EXPRESSION = 114;
    static final int ASTSCALAR_TIMING_CHECK_CONDITION = 115;
    static final int ASTSDPD = 116;
    static final int ASTSDPD_CONDITIONAL_EXPRESSION = 117;
    static final int ASTSEQ_BLOCK = 118;
    static final int ASTSEQUENTIAL_ENTRY = 119;
    static final int ASTSOURCE_TEXT = 120;
    static final int ASTSPECIFY_BLOCK = 121;
    static final int ASTSPECIFY_INPUT_TERMINAL_DESCRIPTOR = 122;
    static final int ASTSPECIFY_ITEM = 123;
    static final int ASTSPECIFY_OUTPUT_TERMINAL_DESCRIPTOR = 124;
    static final int ASTSPECIFY_TERMINAL_DESCRIPTOR = 125;
    static final int ASTSPECPARAM_DECLARATION = 126;
    static final int ASTSTATE = 127;
    static final int ASTSTATEMENT = 128;
    static final int ASTSTATEMENT_OR_NULL = 129;
    static final int ASTSTRENGTH0 = 130;
    static final int ASTSTRENGTH1 = 131;
    static final int ASTSTRING = 132;
    static final int ASTSYSTEM_IDENTIFIER = 133;
    static final int ASTSYSTEM_TASK_ENABLE = 134;
    static final int ASTSYSTEM_TIMING_CHECK = 135;
    static final int ASTTABLE_DEFINITION = 136;
    static final int ASTTABLE_ENTRIES = 137;
    static final int ASTTASK = 138;
    static final int ASTTASK_ENABLE = 139;
    static final int ASTTERMINAL = 140;
    static final int ASTTF_DECLARATION = 141;
    static final int ASTTIME_DECLARATION = 142;
    static final int ASTTIMING_CHECK_CONDITION = 143;
    static final int ASTTIMING_CHECK_EVENT = 144;
    static final int ASTTIMING_CHECK_EVENT_CONTROL = 145;
    static final int ASTTIMING_CHECK_LIMIT = 146;
    static final int ASTUDP = 147;
    static final int ASTUDP_DECLARATION = 148;
    static final int ASTUDP_INITIAL_STATEMENT = 149;
    static final int ASTUDP_INSTANCE = 150;
    static final int ASTUDP_INSTANTIATION = 151;
    static final int ASTUNARY_OPERATOR = 152;
    static final int ASTUNSIGNED_NUMBER = 153;
    static final int ASTVERILOG_TOKEN = 154;
    
    // additional nodes(statements)
    static final int ASTIF_STATEMENT = 155;
    static final int ASTCASE_STATEMENT = 156;
    static final int ASTLOOP_STATEMENT = 157;
    static final int ASTWAIT_STATEMENT = 158;
    static final int ASTDELAY_OR_EVENT_STATEMENT = 159;
    static final int ASTDISABLE_TASK_OR_BLOCK = 160;
    static final int ASTASSIGN_ASSIGNMENT = 161;
    static final int ASTFORCE_ASSIGNMENT = 162;
    static final int ASTDEASSIGN_LVALUE = 163;
    static final int ASTRELEASE_LVALUE = 164;
    static final int ASTEVENT_TRIGGER_STATEMENT = 165;
    static final int ASTBLOCK_ASSIGNMENT_STATEMENT = 166;
    static final int ASTNON_BLOCK_ASSIGNMENT_STATEMENT = 167;
    
    //compiler directive
    static final int ASTCOMPILER_DIRECTIVE       = 168;
    static final int ASTCD_DEFAULT_NETTYPE       = 169;
    static final int ASTCD_DEFINE                = 170;
    static final int ASTCD_IFDEF                 = 171;
    static final int ASTCD_INCLUDE               = 172;
    static final int ASTCD_TIMESCALE             = 173;

    static final String[] ASTNodeName =
    {
        "always_statement",
        "assignment",
        "base",
        "binary_operator",
        "block_declaration",
        "blocking_assignment",
        "case_item",
        "charge_strength",
        "combinational_entry",
        "concatenation",
        "conditional_port_expression",
        "constant_expression",
        "continuous_assign",
        "controlled_timing_check_event",
        "data_source_expression",
        "decimal_number",
        "delay",
        "delay_control",
        "delay_or_event_control",
        "description",
        "drive_strength",
        "edge",
        "edge_control_specifier",
        "edge_descriptor",
        "edge_identifier",
        "edge_input_list",
        "edge_sensitive_path_declaration",
        "edge_symbol",
        "event_control",
        "event_declaration",
        "event_expression",
        "expandrange",
        "expression",
        "function",
        "function_call",
        "gate_declaration",
        "gate_instance",
        "gatetype",
        "identifier",
        "IDENTIFIER",
        "init_val",
        "initial_statement",
        "inout_declaration",
        "input_declaration",
        "input_identifier",
        "input_list",
        "integer_declaration",
        "level_input_list",
        "level_sensitive_path_declaration",
        "level_symbol",
        "list_of_assignments",
        "list_of_module_connections",
        "list_of_param_assignments",
        "list_of_path_inputs",
        "list_of_path_outputs",
        "list_of_ports",
        "list_of_register_variables",
        "list_of_variables",
        "lvalue",
        "mintypmax_expression",
        "module",
        "module_instance",
        "module_instantiation",
        "module_item",
        "module_port_connection",
        "multiple_concatenation",
        "name_of_block",
        "name_of_event",
        "name_of_function",
        "name_of_gate_instance",
        "name_of_instance",
        "name_of_memory",
        "name_of_module",
        "name_of_port",
        "name_of_register",
        "name_of_system_function",
        "name_of_system_task",
        "name_of_task",
        "name_of_udp",
        "name_of_udp_instance",
        "name_of_variable",
        "named_port_connection",
        "net_declaration",
        "nettype",
        "next_state",
        "non_blocking_assignment",
        "notify_register",
        "NULL",
        "number",
        "output_declaration",
        "output_identifier",
        "output_symbol",
        "output_terminal_name",
        "par_block",
        "param_assignment",
        "parameter_declaration",
        "parameter_override",
        "parameter_value_assignment",
        "path_declaration",
        "path_delay_expression",
        "path_delay_value",
        "path_description",
        "polarity_operator",
        "port",
        "port_expression",
        "port_reference",
        "primary",
        "range",
        "range_or_type",
        "real_declaration",
        "reg_declaration",
        "register_variable",
        "scalar_constant",
        "scalar_event_expression",
        "scalar_expression",
        "scalar_timing_check_condition",
        "sdpd",
        "sdpd_conditional_expresssion",
        "seq_block",
        "sequential_entry",
        "source_text",
        "specify_block",
        "specify_input_terminal_descriptor",
        "specify_item",
        "specify_output_terminal_descriptor",
        "specify_terminal_descriptor",
        "specparam_declaration",
        "state",
        "statement",
        "statement_or_null",
        "strength0",
        "strength1",
        "string",
        "system_identifier",
        "system_task_enable",
        "system_timing_check",
        "table_definition",
        "table_entries",
        "task",
        "task_enable",
        "terminal",
        "tf_declaration",
        "time_declaration",
        "timing_check_condition",
        "timing_check_event",
        "timing_check_event_control",
        "timing_check_limit",
        "udp",
        "udp_declaration",
        "udp_initial_statement",
        "udp_instance",
        "udp_instantiation",
        "unary_operator",
        "unsigned_number",
        "verilog_token",
        
        "if_statement",
        "case_statement",
        "loop_statement",
        "wait_statement",
        "delay_or_event_statement",
        "disable_task_or_block",
        "assign_assignment",
        "force_assignment",
        "deassign_lvalue",
        "release_lvalue",
        "event_trigger_statement",
        "block_assignment_statement",
        "non_block_assignment_statement",
        
        "compiler_directive",
        "cd_default_nettype",
        "cd_define",
        "cd_ifdef",
        "cd_include",
        "cd_timescale",
    };
}
