package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * print file and line number
 */
public class MyDebug
{
    static PrintStream stream = System.out;
    public static void init(String path)
    {
        try {
            stream = new PrintStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void exit()
    {
        if(stream != System.out) {
            stream.flush();
            stream.close();
        }
    }
    
    /**
     * print this file and line
     */
    public static void printFileLine()
    {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        String fileName = stacks[2].getFileName();
        String lineNum = String.format("%d", stacks[2].getLineNumber());
        stream.println(fileName + "--" + lineNum);
    }
    
    /**
     * print this file and line, append other string
     */
    public static void printFileLine(String msg)
    {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        String fileName = stacks[2].getFileName();
        String lineNum = String.format("%d", stacks[2].getLineNumber());
        stream.println(fileName + "--" + lineNum + ": " + msg);
    }
    
    /**
     * print calling file and line
     */
    public static void printFileLine0()
    {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        String fileName = stacks[3].getFileName();
        String lineNum = String.format("%d", stacks[3].getLineNumber());
        stream.println(fileName + "--" + lineNum);
    }
    
    /**
     * print calling file and line, append other string
     */
    public static void printFileLine0(String msg)
    {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        String fileName = stacks[3].getFileName();
        String lineNum = String.format("%d", stacks[3].getLineNumber());
        stream.println(fileName + "--" + lineNum + ": " + msg);
    }
    
    public static void printStackTrace()
    {
        stream.println("**********start************");
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        for (int i=0; i < stacks.length; i++)
            stream.println("\tat " + stacks[i]);
        stream.println("**********end************");
    }
    
    public static void printStackTrace(Exception e)
    {
        if(e == null)
            return;
        stream.println("**********start************");
        stream.println(e);
        StackTraceElement[] stacks = e.getStackTrace();
        for (int i=0; i < stacks.length; i++)
            stream.println("\tat " + stacks[i]);

        Throwable ourCause = e.getCause();
        if (ourCause != null)
            stream.println("Caused by: " + ourCause);
        stream.println("**********end************");
    }
}
