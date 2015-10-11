
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import common.MyDebug;

import converter.hdlConverter;
import converter.verilog.Verilog;
import converter.vhdl.Vhdl;

import parser.IParser;
import parser.ParserException;
import parser.vhdl.LibraryManager;

public class hdl2SystemC
{
    public static void printUsage() {
        final String usage = "usage:" + System.lineSeparator() + 
       "   java hdl2SystemC [options] [file_path]" + System.lineSeparator() +
       " ==================================================================================" + System.lineSeparator() + 
       " options:" + System.lineSeparator() + 
       "   -h, -help : display this help" + System.lineSeparator() + 
       "   -libdir  : top directory path of libary, may be absolutely or relative directory name." + System.lineSeparator() + 
       "              1. every sub-directory denote one library, " + System.lineSeparator() +
       "                 library name is name of sub-directory." + System.lineSeparator() + 
       "              2. library named this directory include files " + System.lineSeparator() +
       "                 of this directory(excluding sub-directory) is in " + System.lineSeparator() + 
       "   -logfile : file path which error or warning message are written to. " + System.lineSeparator() + 
       "              if null, write to console" + System.lineSeparator() + 
       "   -dstDir   : destination directory where .cpp&.h are put" + System.lineSeparator() + 
       " file_path: hdl file path, may be directory or file, if file_path is directory, " + System.lineSeparator() +
       "            then convert all files(include sub-directory) in this directory." + System.lineSeparator() +
       " ==================================================================================" + System.lineSeparator()+
       " example:" + System.lineSeparator() +
       "   files in directory d:/vhdlLib are: dir1, dir2, file1, file2" + System.lineSeparator() +
       "   files in dir1 are: file3, file4" + System.lineSeparator() +
       "   files in dir2 are: file5, file6" + System.lineSeparator() +
       "     1. run:" + System.lineSeparator() +
       "         java hdl2SystemC -libdir d:/vhdlLib d:/vhdlLib/file1" + System.lineSeparator() +
       "       result is:" + System.lineSeparator() +
       "          library vhdlLib including file1, file2" + System.lineSeparator() +
       "          library dir1 including file3, file4" + System.lineSeparator() + 
       "          library dir2 including file5, file6" + System.lineSeparator() +
       "          convert file: d:/vhdlLib/file1" + System.lineSeparator() +
       "     2. continue run:" + System.lineSeparator() + 
       "          java hdl2SystemC d:/vhdlLib" + System.lineSeparator() +
       "        will use last step's library to parse and convert file:" + System.lineSeparator() +
       "         convert file: d:/vhdlLib/file1" + System.lineSeparator() + 
       "         convert file: d:/vhdlLib/file1" + System.lineSeparator() + 
       "         convert file: d:/vhdlLib/dir1/file3" + System.lineSeparator() + 
       "         convert file: d:/vhdlLib/dir1/file4" + System.lineSeparator() + 
       "         convert file: d:/vhdlLib/dir2/file5" + System.lineSeparator() + 
       "         convert file: d:/vhdlLib/dir2/file6" + System.lineSeparator() + 
       "    3. run:" + System.lineSeparator() +
       "         java hdl2SystemC -logfile vhdl.txt d:/vhdlLib/file1" + System.lineSeparator() +
       "       convert file: d:/vhdlLib/file1" + System.lineSeparator() +
       "       log message is written to vhdl.txt" + System.lineSeparator();
        
        System.out.print(usage);
    }
    
    public static boolean parseParam(String[] args)
    {
        if(args == null || args.length < 2) {
            printUsage();
            return false;
        }
        
        for(int i = 0; i < args.length; i++) {
            String param = args[i];
            if(param.equalsIgnoreCase("-h") || param.equalsIgnoreCase("-help")) {
                printUsage();
                return false;
            }else if(param.equalsIgnoreCase("-libdir")) {
                i++;
                if(i >= args.length) {
                    printUsage();
                    return false;
                }
                libdir = args[i];
            }else if(param.equalsIgnoreCase("-logfile")) {
                i++;
                if(i >= args.length) {
                    printUsage();
                    return false;
                }
                logFile = args[i];
            }else if(param.equalsIgnoreCase("-dstdir")) {
                i++;
                if(i >= args.length) {
                    printUsage();
                    return false;
                }
                dstdir = args[i];
            }else {
                filePath = args[i];
            }
        }
        return true;
    }

    public static String libdir = "";
    public static String logFile = "";
    public static String filePath = "";
    public static String dstdir = "";
    
    public static void main(String[] args)
    {
        if(!parseParam(args)) {
            return;
        }
        
        hdlConverter conv = null;
        hdlConverter conv1 = null;
        boolean isDir = false;
        
        if(!logFile.isEmpty()) {
            MyDebug.init("vhdllog.txt");
        }
        
        switch(getFileType(filePath))
        {
        case hdlConverter.T_VERILOG:
            conv = new Verilog();
            break;
            
        case hdlConverter.T_VHDL:
            conv = new Vhdl();
            break;
        case hdlConverter.T_DIR:
            conv = new Vhdl();
            conv1 = new Verilog();
            isDir = true;
            break;
        default:
        case hdlConverter.T_NONE:
            System.err.println("file type not support! : " + filePath);
            return;
        }
        
        if(!libdir.isEmpty()) {
            conv.addLibary(libdir, "");
        }
        
        try {
            if(isDir) {
                conv.convertDir(filePath, dstdir);
                conv1.convertDir(filePath, dstdir);
            }else {
                conv.convertFile(filePath, dstdir);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MyDebug.exit();
    }
    
    public static int getFileType(String path)
    {
        File f = new File(path);
        int index = path.lastIndexOf('.');
        if(index > 0)
        {
            String ext = path.substring(index + 1);
            if(ext.equalsIgnoreCase(IParser.EXT_VERILOG))
                return hdlConverter.T_VERILOG;
            else if(ext.equalsIgnoreCase(IParser.EXT_VHDL))
                return hdlConverter.T_VHDL;
            else
                return hdlConverter.T_NONE;
        }
        else
        {
            if(f.exists() && f.isDirectory())
                return hdlConverter.T_DIR;
            else
                return hdlConverter.T_NONE;
        }
    }
}
