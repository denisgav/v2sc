/**
 * 
 * This file is based on the VHDL parser originally developed by
 * (c) 1997 Christoph Grimm,
 * J.W. Goethe-University Frankfurt
 * Department for computer engineering
 *
 **/
package parser.verilog;

import java.util.ArrayList;

import parser.IASTNode;
import parser.INameObject;
import parser.ISymbol;
import parser.Token;

/**
 * A symbol - entry in symbol-table
 */
public class Symbol implements ISymbol, Cloneable
{
    /** invalid symbol kind */
    public static final int KIND_INVALID = -1;
   
    /**
     * symbol name
     */
    public String name;
    
    /**
     * symbol kind<br>
     * available kinds: <br>
     * <b>function</b>, <b>procedure</b>, <b>variable</b>, <b>constant</b>, <b>type</b><br>
     * <b>attribute</b>, <b>alias</b>, <b>subtype</b>, <b>file</b>, <b>group</b><br>
     * <b>signal</b>, <b>component</b>, <b>disconnect</b>, <b>nature</b>, <b>terminal</b><br>
     * <b>subnature</b>, <b>generic</b>, <b>port</b>, <b>units</b>, <b>loop</b>
     * @see VhdlTokenConstants
     */
    public int kind;
    
    /**
     * data type(constant,variable, function return)<br>
    */
    public String type;
    
    /**
     * value range(value limitation)
     */
    public String[] range = null;
    
    /**
     * array range(register array)
     */
    public String[] arrayRange = null;
    
    /**
     * default value
     * @note only save primitive type value(integer, bit, ...)<br>
     * don't use in composite type(record, array, ...)
     */
    public String value = "";
    
    /**
     * param list(only used in function or procedure)
     */
    public ArrayList<String> paramTypeList = null;
    
    public Symbol()
    {
        this("name", KIND_INVALID);
    }
    
    public Symbol(String name)
    {
        this(name, KIND_INVALID);
    }
    
    public Symbol(String name, int kind)
    {
        this(name, kind, "");
    }
    
    public Symbol(String name, int kind, String value)
    {
        this.name = name;
        this.kind = kind;
        this.value = value;
    }
    
    public boolean equals(INameObject other)
    {
        if(other == null || !(other instanceof Symbol)) {
            return false;
        }
        
        return name.equals(other.getName());
    }
    
    @Override
    public Symbol clone() {
        Symbol newSym = new Symbol();
        newSym.kind = kind;
        newSym.name = name;
        newSym.paramTypeList = paramTypeList;
        newSym.range = range;
        newSym.arrayRange = arrayRange;
        newSym.type = type;
        newSym.value = value;
        return newSym;
    }
    
    public void setParamList(ArrayList<String> paramList)
    {
        paramTypeList = paramList;
    }
    
    public void setRangeByNode(IASTNode rangeNode) {
        range = getRange(rangeNode);
    }
    
    public void setArrayRange(IASTNode rangeNode) {
        arrayRange = getRange(rangeNode);
    }
    
    private String[] getRange(IASTNode rangeNode) {
        if(rangeNode == null || rangeNode.getId() != VerilogASTConstants.ASTRANGE) {
            return null;
        }
        
        String[] ran = new String[2];
        
        /* constant_expression  :  constant_expression */
        for(int i = 0; i < 2; i++) {
            ASTNode c = (ASTNode)rangeNode.getChild(0);
            Token tkn = c.getFirstToken();
            ran[i] = "";
            while(tkn != c.getLastToken() && tkn != null) {
                ran[i] += tkn.image;
                tkn = tkn.next;
            }
            
            if(tkn != null) {
                ran[0] += tkn.image;
            }
        }
        return ran;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
       this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

