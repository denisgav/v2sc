package converter.verilog;

import converter.ScConstants;

public interface ScVerilogConstants extends ScConstants
{
    static final String[] verilogTypes = 
    {
    };
    
    /*
     * to more precise modify this value to false
     * normally, if you don't need 'X', 'Z' state
     * you should set to true
     */
    static final boolean fastSimulation = true;
    
    static final int[] replaceTypes = 
    {
    };

    // faster simulation
    static final int[] replaceFastTypes = 
    {
    };
    
    
    static final String[] verilogOperators = 
    {
    };
    

    static final int[] replaceOp = 
    {
   
    };
    
    //vhdl time scale
    static final String[] verilogTimeScale = 
    {
        "ps", "ns", "us", "ms", "s",
    };
}


