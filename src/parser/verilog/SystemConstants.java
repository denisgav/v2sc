package parser.verilog;

/**
 * predefinition of system timing check, task, functions
 */
public interface SystemConstants
{
    // system timing check
    static final String SYS_SETUP       = "$setup";
    static final String SYS_HOLD        = "$hold";
    static final String SYS_PERIOD      = "$period";
    static final String SYS_WIDTH       = "$width";
    static final String SYS_SKEW        = "$skew";
    static final String SYS_RECOVERY    = "$recovery";
    static final String SYS_SETUPHOLD   = "$setuphold";
    
    // system task & functions
    static final String SYS_BITSTOREAL     = "$bitstoreal";
    static final String SYS_COUNTDRIVERS   = "$countdrivers";
    static final String SYS_DISPLAY        = "$display";
    static final String SYS_FCLOSE         = "$fclose";
    static final String SYS_FDISPLAY       = "$fdisplay";
    static final String SYS_FMONITOR       = "$fmonitor";
    static final String SYS_FOPEN          = "$fopen";
    static final String SYS_FSTROBE        = "$fstrobe";
    static final String SYS_FWRITE         = "$fwrite";
    static final String SYS_FINISH         = "$finish";
    static final String SYS_GETPATTERN     = "$getpattern";
    static final String SYS_HISTORY        = "$history";
    static final String SYS_INCSAVE        = "$incsave";
    static final String SYS_INPUT          = "$input";
    static final String SYS_ITOR           = "$itor";
    static final String SYS_KEY            = "$key";
    static final String SYS_LIST           = "$list";
    static final String SYS_LOG            = "$log";
    static final String SYS_MONITOR        = "$monitor";
    static final String SYS_MONITOROFF     = "$monitoroff";
    static final String SYS_MONITORON      = "$monitoron";
    static final String SYS_NOKEY          = "$nokey";
    static final String SYS_NOLOG          = "$nolog";
    static final String SYS_PRINTTIMESCALE = "$printtimescale";
    static final String SYS_READMEMB       = "$readmemb";
    static final String SYS_READMEMH       = "$readmemh";
    static final String SYS_REALTIME       = "$realtime";
    static final String SYS_REALTOBITS     = "$realtobits";
    static final String SYS_RESET          = "$reset";
    static final String SYS_RESET_COUNT    = "$reset_count";
    static final String SYS_RESET_VALUE    = "$reset_value";
    static final String SYS_RESTART        = "$restart";
    static final String SYS_RTOI           = "$rtoi";
    static final String SYS_SAVE           = "$save";
    static final String SYS_SCALE          = "$scale";
    static final String SYS_SCOPE          = "$scope";
    static final String SYS_SHOWSCOPES     = "$showscopes";
    static final String SYS_SHOWVARIABLES  = "$showvariables";
    static final String SYS_SHOWVARS       = "$showvars";
    static final String SYS_SREADMEMB      = "$sreadmemb";
    static final String SYS_SREADMEMH      = "$sreadmemh";
    static final String SYS_STIME          = "$stime";
    static final String SYS_STOP           = "$stop";
    static final String SYS_STROBE         = "$strobe";
    static final String SYS_TIME           = "$time";
    static final String SYS_TIMEFORMAT     = "$timeformat";
    static final String SYS_WRITE          = "$write";
}
