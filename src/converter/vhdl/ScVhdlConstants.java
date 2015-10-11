package converter.vhdl;

import converter.ScConstants;

public interface ScVhdlConstants extends ScConstants
{
    // array direction
    static final String RANGE_TO = "to";
    static final String RANGE_DOWNTO = "downto";
    
    // port direction
    static final String PORT_IN = "in";
    static final String PORT_OUT = "out";    
    static final String PORT_INOUT = "inout";    
   
    static final String SEL_OTHERS = "others";
    
    // vhdl data type
    static final int VHDL_BIT               = 0;
    static final int VHDL_BIT_VECTOR        = 1;
    static final int VHDL_INTEGER           = 2;
    static final int VHDL_STD_LOGIC         = 3;
    static final int VHDL_STD_LOGIC_VECTOR  = 4;
    static final int VHDL_STD_ULOGIC        = 5;
    static final int VHDL_STD_ULOGIC_VECTOR = 6;
    static final int VHDL_BOOLEAN           = 7;
    static final int VHDL_FLOAT             = 8;
    static final int VHDL_NATURAL           = 9;
    static final int VHDL_REAL              = 10;
    static final int VHDL_SIGNED            = 11;
    static final int VHDL_UNSIGNED          = 12;
    static final int VHDL_STRING            = 13;
    
    static final String[] vhdlTypes = 
    {
        "bit", "bit_vector", "integer", 
        "std_logic", "std_logic_vector", "std_ulogic",
        "std_ulogic_vector", "boolean", "float", "natural",
        "real", "signed", "unsigned", "string",
    };
    
    /*
     * to more precise modify this value to false
     * normally, if you don't need 'X', 'Z' state
     * you should set to true
     */
    static final boolean fastSimulation = true;
    
    static final int[] replaceTypes = 
    {
        SC_BIT, SC_BV, SC_C_INT, 
        SC_LOGIC, SC_LV, SC_LOGIC,
        SC_LV, SC_BOOL, SC_FLOAT,
        SC_C_UINT, SC_DOUBLE, SC_C_INT, 
        SC_C_UINT, SC_CHAR,
    };

    // faster simulation
    static final int[] replaceFastTypes = 
    {
        SC_BOOL, SC_UINT, SC_C_INT, 
        SC_BOOL, SC_UINT, SC_BOOL, 
        SC_UINT, SC_BOOL, SC_FLOAT, 
        SC_C_UINT, SC_DOUBLE, SC_C_INT,
        SC_C_UINT, SC_CHAR,
    };
    
    
    // vhdl operator
    static final int VHDL_EXP       = 0;
    static final int VHDL_MOD       = 1;
    static final int VHDL_REM       = 2;
    static final int VHDL_ABS       = 3;
    
    static final int VHDL_SLA       = 4;
    static final int VHDL_SLL       = 5;
    static final int VHDL_SRA       = 6;
    static final int VHDL_SRL       = 7;
    static final int VHDL_ROL       = 8;
    static final int VHDL_ROR       = 9;
    
    static final int VHDL_AND       = 10;
    static final int VHDL_OR        = 11;
    static final int VHDL_NOT       = 12;
    static final int VHDL_XOR       = 13;
    static final int VHDL_NAND      = 14;
    static final int VHDL_NOR       = 15;
    static final int VHDL_XNOR      = 16;
    
    static final int VHDL_EQ        = 17;
    static final int VHDL_NE        = 18;
    static final int VHDL_GT        = 19;
    static final int VHDL_LT        = 20;
    static final int VHDL_GE        = 21;
    static final int VHDL_LE        = 22;
    
    static final String[] vhdlOperators = 
    {
        "**", "mod", "rem",  "abs",                         // arithmetic
        "sla", "sll", "sra", "srl", "rol", "ror",           // shift         
        "and", "or", "not", "xor", "nand", "nor", "xnor",   // bit/logical
        "=", "/=", ">", "<", ">=", "<=",                    // relation
    };
    

    static final int[] replaceOp = 
    {
        SC_EXP, SC_MOD, SC_MOD, SC_ABS,
        SC_SLA, SC_SLL, SC_SRA, SC_SRL, SC_ROL, SC_ROR,
        SC_BIT_AND, SC_BIT_OR, SC_BIT_NOT, SC_XOR, SC_NAND, SC_NOR, SC_XNOR,
        SC_ASSIGN_EQ, SC_NE, SC_GT, SC_LT, SC_GE, SC_LE,        
    };
    
    static final int[] replaceBooleanOp = 
    {
        SC_EXP, SC_MOD, SC_MOD, SC_ABS,
        SC_SLA, SC_SLL, SC_SRA, SC_SRL, SC_ROL, SC_ROR,
        SC_LOGIC_AND, SC_LOGIC_OR, SC_LOGIC_NOT, SC_XOR, SC_NAND, SC_NOR, SC_XNOR,
        SC_LOGIC_EQ, SC_NE, SC_GT, SC_LT, SC_GE, SC_LE,
    };
    
    //vhdl time scale
    static final String[] vhdlTimeScale = 
    {
        "fs", "ps", "ns", "us", "ms", "s",
    };
}


