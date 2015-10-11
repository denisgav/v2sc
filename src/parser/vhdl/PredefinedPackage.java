package parser.vhdl;

class PrePkg
{
    String libName = "";
    String pkgName = "";
    Symbol[] syms = null;
    public PrePkg(String libName, String pkgName, Symbol[] syms) {
        this.libName = libName;
        this.pkgName = pkgName;
        this.syms = syms;
    }
}

/**
 * some standard symbols(type, subtype, function, ...)
 */
public class PredefinedPackage implements VhdlTokenConstants
{
    // library name
    static final String IEEE = "ieee";
    static final String STD = "std";
    
    // package name
    static final String STANDARD = "standard";
    static final String STD_LOGIC_1164 = "std_logic_1164";
    static final String STD_LOGIC_ARITH = "std_logic_arith";
    static final String STD_LOGIC_SIGNED = "std_logic_signed";
    static final String STD_LOGIC_UNSIGNED = "std_logic_unsigned";
    static final String STD_LOGIC_TEXTIO = "std_logic_textio";
    static final String TEXTIO = "textio";
    static final String NUMERIC_BIT = "numeric_bit";
    static final String NUMERIC_STD = "numeric_std";
    static final String MATH_REAL = "math_real";
    static final String MATH_COMPLEX = "math_complex";
    static final String VITAL_TIMING = "vital_timing";
    static final String VITAL_PRIMITIVES = "vital_primitives";
    
    static final Symbol[] standard_syms = 
    {
        new Symbol("severity_level", TYPE),
    };
    
    static final Symbol[] std_logic_1164_syms = 
    {
        //subtype
        new Symbol("x01", SUBTYPE, "std_ulogic", new String[]{"X", "to", "1"}),
        new Symbol("x01Z", SUBTYPE, "std_ulogic", new String[]{"X", "to", "Z"}),
        new Symbol("ux01", SUBTYPE, "std_ulogic", new String[]{"U", "to", "1"}),
        new Symbol("ux01z", SUBTYPE, "std_ulogic", new String[]{"U", "to", "Z"}),
        
        // function
        new Symbol("to_bit", FUNCTION, "bit"),
        new Symbol("to_bitvector", FUNCTION, "bit_vector"),
        new Symbol("to_stdULogic", FUNCTION, "std_ulogic"),
        new Symbol("to_stdLogicVector", FUNCTION, "std_logic_vector"),
        new Symbol("to_stdULogicVector", FUNCTION, "std_ulogic_vector"),
        new Symbol("to_x01", FUNCTION, "x01"),
        new Symbol("to_x01z", FUNCTION, "x01z"),
        new Symbol("to_ux01", FUNCTION, "ux01"),
        new Symbol("rising_edge", FUNCTION, "boolean"),
        new Symbol("falling_edge", FUNCTION, "boolean"),
        new Symbol("is_x", FUNCTION, "boolean"),
        
        // following symbols are not in std_logic_1164, but always be used
        new Symbol("conv_integer", FUNCTION, "integer"),
        new Symbol("conv_std_logic_vector", FUNCTION, "std_logic_vector"),
    };
    
    static final Symbol[] std_logic_arith_syms = 
    {
        new Symbol("unsigned", TYPE, "std_logic"),
        new Symbol("signed", TYPE, "std_logic"),
        new Symbol("small_int", SUBTYPE, "integer"),
        new Symbol("shl", FUNCTION, "unsigned"),
        new Symbol("shl", FUNCTION, "signed"),
        new Symbol("shr", FUNCTION, "unsigned"),
        new Symbol("shr", FUNCTION, "signed"),
        new Symbol("conv_integer", FUNCTION, "integer"),
        new Symbol("conv_integer", FUNCTION, "small_int"),
        new Symbol("conv_unsigned", FUNCTION, "unsigned"),
        new Symbol("conv_signed", FUNCTION, "signed"),

        new Symbol("conv_std_logic_vector", FUNCTION, "std_logic_vector"),
        new Symbol("ext", FUNCTION, "std_logic_vector"),
        new Symbol("sxt", FUNCTION, "std_logic_vector"),
    };
    
    static final Symbol[] std_logic_signed_unsigned_syms =
    {
        new Symbol("shl", FUNCTION, "std_logic_vector"),
        new Symbol("shr", FUNCTION, "std_logic_vector"),
        new Symbol("conv_integer", FUNCTION, "integer"),
    };
    
    static final Symbol[] textio_syms = 
    {
        new Symbol("line", TYPE, "string"),
        new Symbol("text", TYPE, "string"),
        new Symbol("side", TYPE),
        new Symbol("width", SUBTYPE, "natural"),
        new Symbol("input", FILE, "text"),
        new Symbol("output", FILE, "text"),
        new Symbol("read", PROCEDURE),
        new Symbol("write", PROCEDURE),
        new Symbol("writeline", PROCEDURE),
        new Symbol("readline", PROCEDURE),
    };
    
    static final Symbol[] numeric_bit_syms =
    {
        new Symbol("shift_left", FUNCTION),
        new Symbol("shift_right", FUNCTION),
        new Symbol("rotate_left", FUNCTION),
        new Symbol("rotate_right", FUNCTION),
        new Symbol("resize", FUNCTION),
        new Symbol("to_integer", FUNCTION),
        new Symbol("to_unsigned", FUNCTION),
        new Symbol("to_signed", FUNCTION),
        new Symbol("rising_edge", FUNCTION, "boolean"),
        new Symbol("falling_edge", FUNCTION, "boolean"),
    };
    
    static final Symbol[] numeric_std_syms =
    {
        // part of std_logic_1164
        new Symbol("x01", SUBTYPE, "std_ulogic", new String[]{"X", "to", "1"}),
        new Symbol("x01Z", SUBTYPE, "std_ulogic", new String[]{"X", "to", "Z"}),
        new Symbol("ux01", SUBTYPE, "std_ulogic", new String[]{"U", "to", "1"}),
        new Symbol("ux01z", SUBTYPE, "std_ulogic", new String[]{"U", "to", "Z"}),
        new Symbol("to_bit", FUNCTION, "bit"),
        new Symbol("to_bitvector", FUNCTION, "bit_vector"),
        new Symbol("to_stdULogic", FUNCTION, "std_ulogic"),
        new Symbol("to_stdLogicVector", FUNCTION, "std_logic_vector"),
        new Symbol("to_stdULogicVector", FUNCTION, "std_ulogic_vector"),
        new Symbol("to_x01", FUNCTION, "x01"),
        new Symbol("to_x01z", FUNCTION, "x01z"),
        new Symbol("to_ux01", FUNCTION, "ux01"),
        new Symbol("rising_edge", FUNCTION, "boolean"),
        new Symbol("falling_edge", FUNCTION, "boolean"),
        new Symbol("is_x", FUNCTION, "boolean"),
        
        // addition in numeric_std
        new Symbol("shift_left", FUNCTION),
        new Symbol("shift_right", FUNCTION),
        new Symbol("rotate_left", FUNCTION),
        new Symbol("rotate_right", FUNCTION),
        new Symbol("resize", FUNCTION),
        new Symbol("to_integer", FUNCTION),
        new Symbol("to_unsigned", FUNCTION),
        new Symbol("to_signed", FUNCTION),
        new Symbol("std_match", FUNCTION),
        new Symbol("to_01", FUNCTION),
    };
    
    static final Symbol[] math_real_syms =
    {
        // constant
        new Symbol("MATH_E", CONSTANT, "real"),          //2.71828_18284_59045_23536
        new Symbol("MATH_1_OVER_E", CONSTANT, "real"),    //0.36787_94411_71442_32160
        new Symbol("MATH_PI",  CONSTANT, "real"),         //3.14159_26535_89793_23846
        new Symbol("MATH_2_PI", CONSTANT, "real"),        //6.28318_53071_79586_47693
        new Symbol("MATH_1_OVER_PI", CONSTANT, "real"),   //0.31830_98861_83790_67154
        new Symbol("MATH_PI_OVER_2", CONSTANT, "real"),   //1.57079_63267_94896_61923
        new Symbol("MATH_PI_OVER_3", CONSTANT, "real"),   //1.04719_75511_96597_74615
        new Symbol("MATH_PI_OVER_4", CONSTANT, "real"),   //0.78539_81633_97448_30962
        new Symbol("MATH_3_PI_OVER_2", CONSTANT, "real"), //4.71238_89803_84689_85769
        new Symbol("MATH_LOG_OF_2", CONSTANT, "real"),   //0.69314_71805_59945_30942
        new Symbol("MATH_LOG_OF_10", CONSTANT, "real"),   //2.30258_50929_94045_68402
        new Symbol("MATH_LOG2_OF_E", CONSTANT, "real"),   //1.44269_50408_88963_4074
        new Symbol("MATH_LOG10_OF_E", CONSTANT, "real"),  //0.43429_44819_03251_82765
        new Symbol("MATH_SQRT_2", CONSTANT, "real"),      //1.41421_35623_73095_04880
        new Symbol("MATH_1_OVER_SQRT_2", CONSTANT, "real"),    //0.70710_67811_86547_52440
        new Symbol("MATH_SQRT_PI", CONSTANT, "real"),     //1.77245_38509_05516_02730
        new Symbol("MATH_DEG_TO_RAD", CONSTANT, "real"),  //0.01745_32925_19943_29577
        new Symbol("MATH_RAD_TO_DEG", CONSTANT, "real"),  //57.29577_95130_82320_87680
        
        // function
        new Symbol("sign", FUNCTION),
        new Symbol("ceil", FUNCTION),
        new Symbol("floor", FUNCTION),
        new Symbol("round", FUNCTION),
        new Symbol("trunc", FUNCTION),
        new Symbol("realmax", FUNCTION),
        new Symbol("realmin", FUNCTION),
        new Symbol("uniform", PROCEDURE),
        new Symbol("sort", FUNCTION),
        new Symbol("cbrt", FUNCTION),
        new Symbol("log", FUNCTION),
        new Symbol("log2", FUNCTION),
        new Symbol("log10", FUNCTION),
        new Symbol("sin", FUNCTION),
        new Symbol("cos", FUNCTION),
        new Symbol("tan", FUNCTION),
        new Symbol("arcsin", FUNCTION),
        new Symbol("arccos", FUNCTION),
        new Symbol("arctan", FUNCTION),
        new Symbol("sinh", FUNCTION),
        new Symbol("cosh", FUNCTION),
        new Symbol("tanh", FUNCTION),
        new Symbol("arcsinh", FUNCTION),
        new Symbol("arccosh", FUNCTION),
        new Symbol("arctanh", FUNCTION),
    };
    
    static final Symbol[] math_complex_syms = 
    {
        new Symbol("complex", TYPE, "record"), // record
        
        new Symbol("positive_real", SUBTYPE, "real"),
        new Symbol("principal_value", SUBTYPE, "real"),
        new Symbol("complex_polar", TYPE, "record"),   // record
        new Symbol("MATH_CBASE_1", CONSTANT, "complex"),
        new Symbol("MATH_CBASE_J", CONSTANT, "complex"),
        new Symbol("MATH_CZERO", CONSTANT, "complex"),
        
        new Symbol("cmplx", FUNCTION),
        new Symbol("get_pricipal_value", FUNCTION),
        new Symbol("complex_to_polar", FUNCTION),
        new Symbol("polar_to_complex", FUNCTION),
        new Symbol("arg", FUNCTION),
        new Symbol("conj", FUNCTION),
        new Symbol("sort", FUNCTION),
        new Symbol("log", FUNCTION),
        new Symbol("log2", FUNCTION),
        new Symbol("log10", FUNCTION),
        new Symbol("sin", FUNCTION),
        new Symbol("cos", FUNCTION),
        new Symbol("tan", FUNCTION),
        new Symbol("sinh", FUNCTION),
        new Symbol("cosh", FUNCTION),
        new Symbol("tanh", FUNCTION),
    };
    
    static final Symbol[] vital_timing_syms =
    {
        new Symbol("vitaltransitiontype", TYPE),
        new Symbol("vitaldelaytype", SUBTYPE),
        new Symbol("vitaldelaytype01", TYPE, "time"),
        new Symbol("vitaldelaytype01z", TYPE, "time"),
        new Symbol("vitaldelaytype01zx", TYPE, "time"),
        new Symbol("vitaldelayarraytype", TYPE, "vitaldelaytype"),
        new Symbol("vitaldelayarraytype01", TYPE, "vitaldelaytype01"),
        new Symbol("vitaldelayarraytype01z", TYPE, "of vitaldelaytype01z"),
        new Symbol("vitaldelayarraytype01zx", TYPE, "vitaldelaytype01zx"),
        new Symbol("vitalzerodelay", CONSTANT, "vitaldelaytype"),
        new Symbol("vitalzerodelay01", CONSTANT, "vitaldelaytype01"),
        new Symbol("vitalzerodelay01z", CONSTANT, "vitaldelaytype01z"),
        new Symbol("vitalzerodelay01zx", CONSTANT, "vitaldelaytype01zx"),
        new Symbol("vital_level0", ATTRIBUTE, "boolean"),
        new Symbol("vital_level1", ATTRIBUTE, "boolean"),
        new Symbol("std_logic_vector2", SUBTYPE),
        new Symbol("std_logic_vector3", SUBTYPE),
        new Symbol("std_logic_vector4", SUBTYPE),
        new Symbol("std_logic_vector8", SUBTYPE),
        new Symbol("vitaloutputmaptype", TYPE, "std_ulogic"),
        new Symbol("vitalresultmaptype", TYPE, "std_ulogic"),
        new Symbol("vitalresultzmaptype", TYPE, "std_ulogic"),    
        new Symbol("vitaldefaultoutputmap", CONSTANT, "vitaloutputmaptype"),
        new Symbol("vitaldefaultresultmap", CONSTANT, "vitalresultmaptype"),
        new Symbol("vitaldefaultresultzmap", CONSTANT, "vitalresultzmaptype"),
        new Symbol("vitaltimearrayt", TYPE, "time"),
        new Symbol("vitaltimearraypt", TYPE, "access"),
        new Symbol("vitalboolarrayt", TYPE, "boolean"),
        new Symbol("vitalboolarraypt", TYPE, "access"),
        new Symbol("vitallogicarraypt", TYPE, "access"),
        new Symbol("vitaltimingdatatype", TYPE, "record"),
        new Symbol("vitaltimingdatainit", FUNCTION, "vitaltimingdatatype"),
        new Symbol("vitalperioddatatype", TYPE, "record"),
        new Symbol("vitalperioddatainit", CONSTANT, "vitalperioddatatype"),
        new Symbol("vitalglitchkindtype", TYPE),
        new Symbol("vitalglitchdatatype", TYPE, "record"),
        new Symbol("vitalglitchdataarraytype", TYPE, "vitalglitchdatatype"),
        new Symbol("vitalpathtype", TYPE, "record"),
        new Symbol("vitalpath01type", TYPE, "record"),
        new Symbol("vitalpath01ztype", TYPE, "record"),
        new Symbol("vitalpatharraytype", TYPE, "vitalpathtype"),
        new Symbol("vitalpatharray01type", TYPE, "vitalpath01type"),
        new Symbol("vitalpatharray01ztype", TYPE, "vitalpath01ztype"),
        new Symbol("vitaltablesymboltype", TYPE),
        new Symbol("vitaledgesymboltype", SUBTYPE, "vitaltablesymboltype"),
        new Symbol("vitalextendtofilldelay", FUNCTION, "vitaldelaytype01z"),
        new Symbol("vitalextendtofilldelay", FUNCTION, "vitaldelaytype01z"),
        new Symbol("vitalextendtofilldelay", FUNCTION, "vitaldelaytype01z"),
        new Symbol("vitalcalcdelay", FUNCTION, "time"),
        new Symbol("vitalcalcdelay", FUNCTION, "time"),
        new Symbol("vitalcalcdelay", FUNCTION, "time"),
        new Symbol("vitalpathdelay", PROCEDURE),
        new Symbol("vitalpathdelay01", PROCEDURE),
        new Symbol("vitalpathdelay01z", PROCEDURE),
        new Symbol("vitalwiredelay", PROCEDURE),
        new Symbol("vitalwiredelay", PROCEDURE),
        new Symbol("vitalwiredelay", PROCEDURE),
        new Symbol("vitalsignaldelay", PROCEDURE),
        new Symbol("vitalsetupholdcheck", PROCEDURE),
        new Symbol("vitalsetupholdcheck", PROCEDURE),
        new Symbol("vitalrecoveryremovalcheck", PROCEDURE),
        new Symbol("vitalperiodpulsecheck", PROCEDURE),
    };
    
    static final Symbol[] vital_primitives_syms =
    {
        new Symbol("VitalAND", FUNCTION, "std_ulogic"),
        new Symbol("VitalAND2", FUNCTION, "std_ulogic"),
        new Symbol("VitalAND3", FUNCTION, "std_ulogic"),
        new Symbol("VitalAND4", FUNCTION, "std_ulogic"),
        new Symbol("VitalOR", FUNCTION, "std_ulogic"),
        new Symbol("VitalOR2", FUNCTION, "std_ulogic"),
        new Symbol("VitalOR3", FUNCTION, "std_ulogic"),
        new Symbol("VitalOR4", FUNCTION, "std_ulogic"),
        new Symbol("VitalXOR", FUNCTION, "std_ulogic"),
        new Symbol("VitalXOR2", FUNCTION, "std_ulogic"),
        new Symbol("VitalXOR3", FUNCTION, "std_ulogic"),
        new Symbol("VitalXOR4", FUNCTION, "std_ulogic"),
        new Symbol("VitalNAND", FUNCTION, "std_ulogic"),
        new Symbol("VitalNAND2", FUNCTION, "std_ulogic"),
        new Symbol("VitalNAND3", FUNCTION, "std_ulogic"),
        new Symbol("VitalNAND4", FUNCTION, "std_ulogic"),
        new Symbol("VitalNOR", FUNCTION, "std_ulogic"),
        new Symbol("VitalNOR2", FUNCTION, "std_ulogic"),
        new Symbol("VitalNOR3", FUNCTION, "std_ulogic"),
        new Symbol("VitalNOR4", FUNCTION, "std_ulogic"),
        new Symbol("VitalXNOR", FUNCTION, "std_ulogic"),
        new Symbol("VitalXNOR2", FUNCTION, "std_ulogic"),
        new Symbol("VitalXNOR3", FUNCTION, "std_ulogic"),
        new Symbol("VitalXNOR4", FUNCTION, "std_ulogic"),
        new Symbol("VitalBUF", FUNCTION, "std_ulogic"),
        new Symbol("VitalBufIf0", FUNCTION, "std_ulogic"),
        new Symbol("VitalBufIf1", FUNCTION, "std_ulogic"),
        new Symbol("VitalINV", FUNCTION, "std_ulogic"),
        new Symbol("VitalInvIf0VitalInvIf1", FUNCTION, "std_ulogic"),
        new Symbol("VitalMux", FUNCTION, "std_ulogic"),
        new Symbol("VitalMux2", FUNCTION, "std_ulogic"),
        new Symbol("VitalMux3", FUNCTION, "std_ulogic"),
        new Symbol("VitalMux4", FUNCTION, "std_ulogic"),
        new Symbol("VitalDecoder", FUNCTION, "std_ulogic"),
        new Symbol("VitalDecoder2", FUNCTION, "std_ulogic"),
        new Symbol("VitalDecoder4", FUNCTION, "std_ulogic"),
        new Symbol("VitalDecoder8", FUNCTION, "std_ulogic"),
        new Symbol("VitalIDENT", FUNCTION, "std_ulogic"),
        new Symbol("VitalTruthTable", FUNCTION, "std_ulogic"),
        new Symbol("VitalStateTable", FUNCTION, "std_ulogic"),
    };
    
    static final PrePkg pkg_standard = new PrePkg(STD, STANDARD, std_logic_1164_syms);
    static final PrePkg pkg_std_logic_1164 = new PrePkg(IEEE, STD_LOGIC_1164, std_logic_1164_syms);
    static final PrePkg pkg_std_logic_arith = new PrePkg(IEEE, STD_LOGIC_ARITH, std_logic_arith_syms);
    static final PrePkg pkg_std_logic_signed = new PrePkg(IEEE, STD_LOGIC_SIGNED, std_logic_signed_unsigned_syms);
    static final PrePkg pkg_std_logic_unsigned = new PrePkg(IEEE, STD_LOGIC_UNSIGNED, std_logic_signed_unsigned_syms);
    static final PrePkg pkg_std_logic_textio = new PrePkg(IEEE, STD_LOGIC_TEXTIO, textio_syms);
    static final PrePkg pkg_textio = new PrePkg(STD, TEXTIO, textio_syms);
    static final PrePkg pkg_numeric_bit = new PrePkg(IEEE, NUMERIC_BIT, numeric_bit_syms);
    static final PrePkg pkg_numeric_std = new PrePkg(IEEE, NUMERIC_STD, numeric_std_syms);
    static final PrePkg pkg_math_real = new PrePkg(IEEE, MATH_REAL, math_real_syms);
    static final PrePkg pkg_math_complex = new PrePkg(IEEE, MATH_COMPLEX, math_real_syms);
    static final PrePkg pkg_vital_timing = new PrePkg(IEEE, VITAL_TIMING, vital_timing_syms);
    static final PrePkg pkg_vital_primitives = new PrePkg(IEEE, VITAL_PRIMITIVES, vital_primitives_syms);
    
    static final PrePkg[] predefined_pkgs = 
    {
        pkg_standard,
        pkg_std_logic_1164,
        pkg_std_logic_arith,
        pkg_std_logic_signed,
        pkg_std_logic_unsigned,
        pkg_std_logic_textio,
        pkg_textio,
        pkg_numeric_bit,
        pkg_numeric_std,
        pkg_math_real,
        pkg_math_complex,
        pkg_vital_timing,
        pkg_vital_primitives,
    };
}
