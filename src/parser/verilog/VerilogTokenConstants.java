
package parser.verilog;


/**
 * keywords and operator
 */
public interface VerilogTokenConstants
{
    // keywords
    static final int ALWAYS        = 0;  
    static final int AND           = 1;  
    static final int ASSIGN        = 2;  
    static final int BEGIN         = 3;  
    static final int BUF           = 4;  
    static final int BUFIF0        = 5;  
    static final int BUFIF1        = 6;  
    static final int CASE          = 7;  
    static final int CASEX         = 8;  
    static final int CASEZ         = 9;  
    static final int CMOS          = 10; 
    static final int DEASSIGN      = 11; 
    static final int DEFAULT       = 12; 
    static final int DEFPARAM      = 13; 
    static final int DISABLE       = 14; 
    static final int EDGE          = 15; 
    static final int ELSE          = 16; 
    static final int END           = 17; 
    static final int ENDCASE       = 18; 
    static final int ENDMODULE     = 19; 
    static final int ENDFUNCTION   = 20; 
    static final int ENDPRIMITIVE  = 21; 
    static final int ENDSPECIFY    = 22; 
    static final int ENDTABLE      = 23; 
    static final int ENDTASK       = 24; 
    static final int EVENT         = 25; 
    static final int FOR           = 26; 
    static final int FORCE         = 27; 
    static final int FOREVER       = 28; 
    static final int FORK          = 29; 
    static final int FUNCTION      = 30; 
    static final int HIGHZ0        = 31; 
    static final int HIGHZ1        = 32; 
    static final int IF            = 33; 
    static final int INITIAL       = 34; 
    static final int INOUT         = 35; 
    static final int INPUT         = 36; 
    static final int INTEGER       = 37; 
    static final int JOIN          = 38; 
    static final int LARGE         = 39; 
    static final int MACROMODULE   = 40; 
    static final int MEDIUM        = 41; 
    static final int MODULE        = 42; 
    static final int NAND          = 43; 
    static final int NEGEDGE       = 44; 
    static final int NMOS          = 45; 
    static final int NOR           = 46; 
    static final int NOT           = 47; 
    static final int NOTIF0        = 48; 
    static final int NOTIF1        = 49; 
    static final int OR            = 50; 
    static final int OUTPUT        = 51; 
    static final int PARAMETER     = 52; 
    static final int PMOS          = 53; 
    static final int POSEDGE       = 54; 
    static final int PRIMITIVE     = 55; 
    static final int PULL0         = 56; 
    static final int PULL1         = 57; 
    static final int PULLUP        = 58; 
    static final int PULLDOWN      = 59; 
    static final int RCMOS         = 60; 
    static final int REG           = 61; 
    static final int RELEASE       = 62; 
    static final int REPEAT        = 63; 
    static final int RNMOS         = 64; 
    static final int RPMOS         = 65; 
    static final int RTRAN         = 66; 
    static final int RTRANIF0      = 67; 
    static final int RTRANIF1      = 68; 
    static final int SCALARED      = 69; 
    static final int SMALL         = 70; 
    static final int SPECIFY       = 71; 
    static final int SPECPARAM     = 72; 
    static final int STRENGTH      = 73; 
    static final int STRONG0       = 74; 
    static final int STRONG1       = 75; 
    static final int SUPPLY0       = 76; 
    static final int SUPPLY1       = 77; 
    static final int TABLE         = 78; 
    static final int TASK          = 79; 
    static final int TIME          = 80; 
    static final int TRAN          = 81; 
    static final int TRANIF0       = 82; 
    static final int TRANIF1       = 83; 
    static final int TRI           = 84; 
    static final int TRI0          = 85; 
    static final int TRI1          = 86; 
    static final int TRIAND        = 87; 
    static final int TRIOR         = 88; 
    static final int TRIREG        = 89; 
    static final int VECTORED      = 90; 
    static final int WAIT          = 91; 
    static final int WAND          = 92; 
    static final int WEAK0         = 93; 
    static final int WEAK1         = 94; 
    static final int WHILE         = 95; 
    static final int WIRE          = 96; 
    static final int WOR           = 97; 
    static final int XNOR          = 98; 
    static final int XOR           = 99; 
    
    // operator
    static final int LBRACE        = 100;   // {
    static final int RBRACE        = 101;   // }
    static final int ADD           = 102;   // +
    static final int SUB           = 103;   // -
    static final int MUL           = 104;   // *
    static final int DIV           = 105;   // /
    static final int MOD           = 106;   // %
    static final int GT            = 107;   // >
    static final int GE            = 108;   // >=
    static final int LO            = 109;   // <
    static final int LE            = 110;   // <=
    static final int LOGIC_NEG     = 111;   // !
    static final int LOGIC_AND     = 112;   // &&
    static final int LOGIC_OR      = 113;   // ||
    static final int LOGIC_EQ      = 114;   // ==
    static final int LOGIC_NEQ     = 115;   // !=
    static final int CASE_EQ       = 116;   // ===
    static final int CASE_NEQ      = 117;   // !==
    static final int BIT_NEG       = 118;   // ~
    static final int BIT_AND       = 119;   // &
    static final int BIT_OR        = 120;   // |
    static final int BIT_XOR       = 121;   // ^
    static final int BIT_XORN      = 122;   // ^~
    static final int BIT_NXOR      = 123;   // ~^
    //static final int REDUCE_AND    = 124;   // &(!!NOT USED!!)
    static final int REDUCE_NAND   = 125;   // ~&
    //static final int REDUCE_OR     = 126;   // |(!!NOT USED!!)
    static final int REDUCE_NOR    = 127;   // ~|
    //static final int REDUCE_XOR    = 128;   // ^(!!NOT USED!!)
    //static final int REDUCE_NXOR   = 129;   // ~^(!!NOT USED!!)
    //static final int REDUCE_XORN   = 130;   // ^~(!!NOT USED!!)
    static final int SHIFT_LEFT    = 131;   // <<
    static final int SHIFT_RIGHT   = 132;   // >>
    static final int QUESTION      = 133;   // ?
    static final int COLON         = 134;   // :
    
    
    // comment
    static final int BLOCK_COMMENT_START  = 135;   // /*
    static final int BLOCK_COMMENT_END    = 136;   // */
    static final int LINE_COMMENT  = 137;   // //
    
    // special token
    static final int COMMA         = 138;   // ,
    static final int SEMICOLON     = 139;   // ;
    static final int ACCENT_GRAVE  = 140;   // `
    static final int AT            = 141;   // @
    static final int DOLLAR        = 142;   // $
    static final int PARA          = 143;   // #
    static final int LPARENTHESIS  = 144;   // (
    static final int RPARENTHESIS  = 145;   // )
    static final int LSQUARE_BRACKET  = 146;   // [
    static final int RSQUARE_BRACKET  = 147;   // ]
    static final int POINT         = 148;   // .
    static final int EQ            = 149;   // =

    static final int EQ_ARROW      = 150;   // =>
    static final int STAR_ARROW    = 151;   // *>
    static final int HYPHEN_ARROW  = 152;   // ->
    
    static final int TRI_AND       = 153;   // &&&
    static final int REAL          = 154;   // real
    
    // edge
    //    01 transition from 0 to 1
    //    0x transition from 0 to x
    //    10 transition from 1 to 0
    //    1x transition from 1 to x
    //    x0 transition from x to 0
    //    x1 transition from x to 1
    static final int EDGE_01 = 155;
    static final int EDGE_0x = 156;
    static final int EDGE_10 = 157;
    static final int EDGE_1x = 158;
    static final int EDGE_x0 = 159;
    static final int EDGE_x1 = 160;
    
    static final int string_lexical = 201;
    static final int number_lexical = 202;
    static final int IDENTIFIER     = 203;
    static final int compiler_directive = 204;
    static final int level_symbol_array = 205;
    
    static final int MACRO_DEFINE = 206;
    
    /** Literal token values. */
    static final String[] tokenImage =
    {
        "always",
        "and",
        "assign",
        "begin",
        "buf",
        "bufif0",
        "bufif1",
        "case",
        "casex",
        "casez",
        "cmos",
        "deassign",
        "default",
        "defparam",
        "disable",
        "edge",
        "else",
        "end",
        "endcase",
        "endmodule",
        "endfunction",
        "endprimitive",
        "endspecify",
        "endtable",
        "endtask",
        "event",
        "for",
        "force",
        "forever",
        "fork",
        "function",
        "highz0",
        "highz1",
        "if",
        "initial",
        "inout",
        "input",
        "integer",
        "join",
        "large",
        "macromodule",
        "medium",
        "module",
        "nand",
        "negedge",
        "nmos",
        "nor",
        "not",
        "notif0",
        "notif1",
        "or",
        "output",
        "parameter",
        "pmos",
        "posedge",
        "primitive",
        "pull0",
        "pull1",
        "pullup",
        "pulldown",
        "rcmos",
        "reg",
        "release",
        "repeat",
        "rnmos",
        "rpmos",
        "rtran",
        "rtranif0",
        "rtranif1",
        "scalared",
        "small",
        "specify",
        "specparam",
        "strength",
        "strong0",
        "strong1",
        "supply0",
        "supply1",
        "table",
        "task",
        "time",
        "tran",
        "tranif0",
        "tranif1",
        "tri",
        "tri0",
        "tri1",
        "triand",
        "trior",
        "trireg",
        "vectored",
        "wait",
        "wand",
        "weak0",
        "weak1",
        "while",
        "wire",
        "wor",
        "xnor",
        "xor",
        
        "{",
        "}",
        "+",
        "-",
        "*",
        "/",
        "%",
        ">",
        ">=",
        "<",
        "<=",
        "!",
        "&&",
        "||",
        "==",
        "!=",
        "===",
        "!==",
        "~",
        "&",
        "|",
        "^",
        "^~",
        "~^",
        "&",
        "~&",
        "|",
        "~|",
        "^",
        "~^",
        "^~",
        "<<",
        ">>",
        "?",
        ":",
        "/*",
        "*/",
        "//",
        ",",
        ";",
        "`",
        "@",
        "$",
        "#",
        "(",
        ")",
        "[",
        "]",
        ".",
        "=",
        "=>",
        "*>",
        "->",
        "&&&",
        "real",
        
        "01",
        "0x",
        "10",
        "1x",
        "x0",
        "x1",
    };
}
