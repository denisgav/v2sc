
package parser.vhdl;


/**
 * Token literal values and constants.
 */
public interface VhdlTokenConstants
{
    static final int COMMENT       = 0;
    static final int ABS           = 1;
    static final int ACCESS        = 2;  
    static final int ACROSS        = 3;  
    static final int AFTER         = 4;  
    static final int ALIAS         = 5;  
    static final int ALL           = 6;  
    static final int AND           = 7;  
    static final int ARCHITECTURE  = 8;  
    static final int ARRAY         = 9;  
    static final int ASSERT        = 10; 
    static final int ATTRIBUTE     = 11; 
    static final int BEGIN         = 12; 
    static final int BLOCK         = 13; 
    static final int BODY          = 14; 
    static final int BREAK         = 15; 
    static final int BUFFER        = 16; 
    static final int BUS           = 17; 
    static final int CASE          = 18; 
    static final int COMPONENT     = 19; 
    static final int CONFIGURATION = 20;  
    static final int CONSTANT      = 21; 
    static final int DISCONNECT    = 22; 
    static final int DOWNTO        = 23; 
    static final int ELSE          = 24; 
    static final int ELSIF         = 25; 
    static final int END           = 26; 
    static final int ENTITY        = 27; 
    static final int EXIT          = 28; 
    static final int FILE          = 29; 
    static final int FOR           = 30; 
    static final int FUNCTION      = 31; 
    static final int GENERATE      = 32; 
    static final int GENERIC       = 33; 
    static final int GROUP         = 34; 
    static final int GUARDED       = 35; 
    static final int IF            = 36; 
    static final int IMPURE        = 37; 
    static final int IN            = 38; 
    static final int INERTIAL      = 39; 
    static final int INOUT         = 40; 
    static final int IS            = 41; 
    static final int LABEL         = 42; 
    static final int LIBRARY       = 43; 
    static final int LIMIT         = 44; 
    static final int LINKAGE       = 45; 
    static final int LITERAL       = 46; 
    static final int LOOP          = 47; 
    static final int MAP           = 48; 
    static final int MOD           = 49; 
    static final int NAND          = 50; 
    static final int NATURE        = 51; 
    static final int NEW           = 52; 
    static final int NEXT          = 53; 
    static final int NOISE         = 54; 
    static final int NOR           = 55; 
    static final int NOT           = 56; 
    static final int NULL          = 57; 
    static final int OF            = 58; 
    static final int ON            = 59; 
    static final int OPEN          = 60; 
    static final int OR            = 61; 
    static final int OTHERS        = 62; 
    static final int OUT           = 63; 
    static final int PACKAGE       = 64; 
    static final int PORT          = 65; 
    static final int POSTPONED     = 66; 
    static final int PROCEDURAL    = 67; 
    static final int PROCEDURE     = 68; 
    static final int PROCESS       = 69; 
    static final int PURE          = 70; 
    static final int QUANTITY      = 71; 
    static final int RANGE         = 72; 
    static final int RECORD        = 73; 
    static final int REFERENCE     = 74; 
    static final int REGISTER      = 75; 
    static final int REJECT        = 76; 
    static final int REM           = 77; 
    static final int REPORT        = 78; 
    static final int RETURN        = 79; 
    static final int ROL           = 80; 
    static final int ROR           = 81; 
    static final int SELECT        = 82; 
    static final int SEVERITY      = 83; 
    static final int SHARED        = 84; 
    static final int SIGNAL        = 85; 
    static final int SLA           = 86; 
    static final int SLL           = 87; 
    static final int SPECTRUM      = 88; 
    static final int SRA           = 89; 
    static final int SRL           = 90; 
    static final int SUBNATURE     = 91; 
    static final int SUBTYPE       = 92; 
    static final int TERMINAL      = 93; 
    static final int THEN          = 94; 
    static final int THROUGH       = 95; 
    static final int TO            = 96; 
    static final int TOLERANCE     = 97; 
    static final int TRANSPORT     = 98; 
    static final int TYPE          = 99; 
    static final int UNAFFECTED    = 100;
    static final int UNITS         = 101;
    static final int UNTIL         = 102;
    static final int USE           = 103;
    static final int VARIABLE      = 104;
    static final int WAIT          = 105;
    static final int WHEN          = 106;
    static final int WHILE         = 107;
    static final int WITH          = 108;
    static final int XNOR          = 109;
    static final int XOR           = 110;
    
    // use exp as the first token of symbol
    static final int EXP           = 111;   // **
    static final int MUL           = 112;   // *
    static final int DIV           = 113;   // /
    static final int ADD           = 114;   // +
    static final int SUB           = 115;   // -
    static final int CONCAT        = 116;   // &
    static final int EQ2           = 117;   // ==
    static final int EQ            = 118;   // =
    static final int NEQ           = 119;   // /=
    static final int GE            = 120;   // >=
    static final int LE            = 121;   // <=
    static final int GT            = 122;   // >
    static final int LO            = 123;   // <
    static final int SEMICOLON     = 124;   // ;
    static final int COLON         = 125;   // :
    static final int ASSIGN        = 126;   // :=
    static final int RARROW        = 127;   // =>
    static final int LBRACKET      = 128;   // (
    static final int RBRACKET      = 129;   // )
    static final int COMMA         = 130;   // ,
    static final int SQUOTE        = 131;   // '
    static final int PIPE          = 132;   // |
    static final int INFINITE      = 133;   // <>
    static final int POINT         = 134;   // .
   
    /**
     * non keywords or delimiters
     */
    static final int identifier         = 201;  // name of function, variable...
    static final int decimal_literal    = 202;
    static final int based_literal      = 205;
    static final int character_literal  = 208;   
    static final int string_literal     = 209;
    static final int bit_string_literal = 210;
  
    /** Literal token values. */
    static final String[] tokenImage =
    {
        "--",
        "abs",
        "access",
        "across",
        "after",
        "alias",
        "all",
        "and",
        "architecture",
        "array",
        "assert",
        "attribute",
        "begin",
        "block",
        "body",
        "break",
        "buffer",
        "bus",
        "case",
        "component",
        "configuration",
        "constant",
        "disconnect",
        "downto",
        "else",
        "elsif",
        "end",
        "entity",
        "exit",
        "file",
        "for",
        "function",
        "generate",
        "generic",
        "group",
        "guarded",
        "if",
        "impure",
        "in",
        "inertial",
        "inout",
        "is",
        "label",
        "library",
        "limit",
        "linkage",
        "literal",
        "loop",
        "map",
        "mod",
        "nand",
        "nature",
        "new",
        "next",
        "noise",
        "nor",
        "not",
        "null",
        "of",
        "on",
        "open",
        "or",
        "others",
        "out",
        "package",
        "port",
        "postponed",
        "procedural",
        "procedure",
        "process",
        "pure",
        "quantity",
        "range",
        "record",
        "reference",
        "register",
        "reject",
        "rem",
        "report",
        "return",
        "rol",
        "ror",
        "select",
        "severity",
        "shared",
        "signal",
        "sla",
        "sll",
        "spectrum",
        "sra",
        "srl",
        "subnature",
        "subtype",
        "terminal",
        "then",
        "through",
        "to",
        "tolerance",
        "transport",
        "type",
        "unaffected",
        "units",
        "until",
        "use",
        "variable",
        "wait",
        "when",
        "while",
        "with",
        "xnor",
        "xor",
    
        "**",
        "*",
        "/",
        "+",
        "-",
        "&",
        "==",
        "=",
        "/=",
        ">=",
        "<=",
        ">",
        "<",
        ";",
        ":",
        ":=",
        "=>",
        "(",
        ")",
        ",",
        "\'",
        "|",
        "<>",
        ".",
    };
}
