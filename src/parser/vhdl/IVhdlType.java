package parser.vhdl;

public interface IVhdlType
{
    /** vhdl basic data type  */
    public static final int TYPE_BIT               = 0;
    public static final int TYPE_BIT_VECTOR        = 1;
    public static final int TYPE_INTEGER           = 2;
    public static final int TYPE_STD_LOGIC         = 3;
    public static final int TYPE_STD_LOGIC_VECTOR  = 4;
    public static final int TYPE_STD_ULOGIC        = 5;
    public static final int TYPE_STD_ULOGIC_VECTOR = 6;
    public static final int TYPE_BOOLEAN           = 7;
    public static final int TYPE_FLOAT             = 8;
    public static final int TYPE_NATURAL           = 9;
    public static final int TYPE_RECORD            = 10;
    public static final int TYPE_PHYSICAL          = 11;
    public static final int TYPE_ENUMERATION       = 11;
    
    String[] strVhdlType = 
    {
        "bit", "bit_vector", "integer",
        "std_logic", "std_logic_vector",
        "std_ulogic", "std_ulogic_vector",
        "boolean", "float", "natural", "record",
        "units", "enumeration"
    };
}
