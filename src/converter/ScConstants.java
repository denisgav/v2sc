package converter;

public interface ScConstants
{
    // symbol type
    static final int SC_INVALID_TYPE    = -1;    
    static final int SC_BIT             = 0;
    static final int SC_BV              = 1;
    static final int SC_C_INT           = 2;
    static final int SC_INT             = 3;
    static final int SC_LOGIC           = 4;
    static final int SC_LV              = 5;
    static final int SC_BOOL            = 6;
    static final int SC_FLOAT           = 7;
    static final int SC_C_UINT          = 8;
    static final int SC_UINT            = 9;
    static final int SC_DOUBLE          = 10;
    static final int SC_CHAR            = 11;
    
    static final int SC_BUILDIN_TYPE    = 12;
    
    static final int SC_STRUCTURE       = 13;
    static final int SC_SUBPROGRAM      = 14;
    static final int SC_ENUME           = 15;
    static final int SC_COMPONENT       = 16;
    
    static final String[] scType = 
    {
        "sc_bit", "sc_bv", "int", "sc_int",
        "sc_logic", "sc_lv", "bool",
        "float", "unsigned int", "sc_uint",
        "double", "char"
    };
    
    // c/c++ language operator
    static final int SC_MOD         = 0;
    static final int SC_SLL         = 1;
    static final int SC_SLA         = 1;    // the same as sll
    static final int SC_SRL         = 2;
    static final int SC_BIT_AND     = 3;
    static final int SC_BIT_OR      = 4;
    static final int SC_BIT_NOT     = 5;
    static final int SC_XOR         = 6;
    static final int SC_LOGIC_AND   = 7;
    static final int SC_LOGIC_OR    = 8;
    static final int SC_LOGIC_NOT   = 9;
    static final int SC_ASSIGN_EQ   = 10;
    static final int SC_LOGIC_EQ    = 11;    
    static final int SC_NE          = 12;
    static final int SC_GT          = 13;
    static final int SC_LT          = 14;
    static final int SC_GE          = 15;
    static final int SC_LE          = 16;
    static final int SC_EXP         = 17;
    
    static final int SC_SEPERATOR   = 18;   // empty
    
    // c/c++ base operator function    
    static final int SC_ABS         = 19;
    static final int SC_SRA         = 10;
    static final int SC_ROL         = 21;
    static final int SC_ROR         = 22;
    static final int SC_NAND        = 23;
    static final int SC_NOR         = 24;
    static final int SC_XNOR        = 25;
    static final String[] scOperators = 
    {
        "%", " << ", " >> ", " & ", " | ", "~", "^", 
        " && ", " || ", "!", " = ", " == ", " != ", " > ", " < ", 
        " >= ", " <= ", " << ", 
        "/*-----*/",
        "abs", "sra", "rol", "ror", "nand", "nor", "xnor",
    };
    
    // tab size (number of space)
    static final int tabSize = 4;
    
    // time scale
    static final int SC_FS = 0;
    static final int SC_PS = 1;
    static final int SC_NS = 2;
    static final int SC_US = 3;
    static final int SC_MS = 4;
    static final int SC_S  = 5;
    static final String[] scTimeScale = 
    {
        "SC_FS", "SC_PS", "SC_NS", "SC_US", "SC_MS", "SC_S"
    };
}
