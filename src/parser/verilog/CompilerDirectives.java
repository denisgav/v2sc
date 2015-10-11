package parser.verilog;

/**
 * compiler directives
 */
public interface CompilerDirectives
{
    static final int CD_ACCELERATE            = 0 ;
    static final int CD_AUTOEXPAND_VECTORNETS = 1 ;
    static final int CD_CELLDEFINE            = 2 ;
    static final int CD_DEFAULT_NETTYPE       = 3 ;
    static final int CD_DEFINE                = 4 ;
    static final int CD_ELSE                  = 5 ;
    static final int CD_ENDCELLDEFINE         = 6 ;
    static final int CD_ENDIF                 = 7 ;
    static final int CD_ENDPROTECT            = 8 ;
    static final int CD_ENDPROTECTED          = 9 ;
    static final int CD_EXPAND_VECTORNETS     = 10;
    static final int CD_IFDEF                 = 11;
    static final int CD_INCLUDE               = 12;
    static final int CD_NOACCELERATE          = 13;
    static final int CD_NOEXPAND_VECTORNETS   = 14;
    static final int CD_NOREMOVE_GATENAMES    = 15;
    static final int CD_NOREMOVE_NETNAMES     = 16;
    static final int CD_NOUNCONNECTED_DRIVE   = 17;
    static final int CD_PROTECT               = 18;
    static final int CD_PROTECTED             = 19;
    static final int CD_REMOVE_GATENAMES      = 20;
    static final int CD_REMOVE_NETNAMES       = 21;
    static final int CD_RESETALL              = 22;
    static final int CD_TIMESCALE             = 23;
    static final int CD_UNCONNECTED_DRIVE     = 24;
    
    static final String[] cdStrings = 
    {
        "`accelerate",
        "`autoexpand_vectornets",
        "`celldefine",
        "`default_nettype",
        "`define",
        "`else",
        "`endcelldefine",
        "`endif",
        "`endprotect",
        "`endprotected",
        "`expand_vectornets",
        "`ifdef",
        "`include",
        "`noaccelerate",
        "`noexpand_vectornets",
        "`noremove_gatenames",
        "`noremove_netnames",
        "`nounconnected_drive",
        "`protect",
        "`protected",
        "`remove_gatenames",
        "`remove_netnames",
        "`resetall",
        "`timescale",
        "`unconnected_drive",
    };
}


