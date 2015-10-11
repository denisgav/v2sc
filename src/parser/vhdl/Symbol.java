/**
 * 
 * This file is based on the VHDL parser originally developed by
 * (c) 1997 Christoph Grimm,
 * J.W. Goethe-University Frankfurt
 * Department for computer engineering
 *
 **/
package parser.vhdl;

import java.util.ArrayList;

import parser.INameObject;
import parser.ISymbol;

/**
 * A symbol - entry in symbol-table
 */
public class Symbol implements ISymbol, Cloneable
{
    /** invalid symbol kind */
    public static final int KIND_INVALID = -1;
    
    //public String libName;
    //public String pkgName;
   
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
     * array range(only valid in array type)
     */
    public String[] arrayRange = null;
    
    /**
     * type range(bitwidth of type)
     */
    public String[] typeRange = null;
    
    /**
     * port mode(in/out/...)
     */
    public String mode = "";
    
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
    
    public Symbol(String name, int kind)
    {
        this(name, kind, "");
    }
    
    public Symbol(String name, int kind, String type)
    {
        this(name, kind, type, "");
    }
    
    public Symbol(String name, int kind, String type, String value)
    {
        this(name, kind, type, value, null, null);
    }
    
    public Symbol(String name, int kind, String type, String[] range)
    {
        this(name, kind, type, "", null, range);
    }
    
    public Symbol(String name, int kind, String type, String value, 
            String[] typeRange, String[] range)
    {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.value = value;
        this.typeRange = typeRange;
        this.range = range;
    }
    
    public boolean equals(INameObject other)
    {
        if(!(other instanceof Symbol)) {
            return false;
        }
        
        Symbol oth = (Symbol)other;
        if(kind != VhdlTokenConstants.FUNCTION 
                && kind != VhdlTokenConstants.PROCEDURE) {
            return name.equalsIgnoreCase(oth.name);
        }else {
            // function or procedure may overload
            boolean ret = (name.equalsIgnoreCase(oth.name));
            if(!ret)
                return false;
            ret = (kind == oth.kind && type.equalsIgnoreCase(oth.type));
            //String msg = "two functions: " + name + ", have the same name, " +
            //"but they are not overload";
            if(!ret) {
                //System.err.println(msg);
                return false;
            }
            
            // check typeRange
            if((typeRange == null && oth.typeRange != null) 
                    || (typeRange != null && oth.typeRange == null)) {
                //System.err.println(msg);
                return false;
            }
            if(typeRange != null && oth.typeRange != null) {
                if(!(typeRange[0].equalsIgnoreCase(oth.typeRange[0])
                        || typeRange[1].equalsIgnoreCase(oth.typeRange[1])
                        || typeRange[2].equalsIgnoreCase(oth.typeRange[2]))) {
                    //System.err.println(msg);
                    return false;
                }
            }
            
            // check parameter list
            if(paramTypeList != null && oth.paramTypeList != null) {
                if(paramTypeList.size() != oth.paramTypeList.size()) {
                    return false;
                }
                for(int i = 0; i < paramTypeList.size(); i++) {
                    if(!paramTypeList.get(i).equalsIgnoreCase(oth.paramTypeList.get(i))) {
                        return false;
                    }
                }
            }else if (paramTypeList == oth.paramTypeList) {
                ret = true;
            }
            return ret;
        }
    }
    
    @Override
    public Symbol clone() {
        Symbol newSym = new Symbol();
        newSym.kind = kind;
        newSym.mode = mode;
        newSym.name = name;
        newSym.paramTypeList = paramTypeList;
        newSym.range = range;
        newSym.type = type;
        newSym.value = value;
        newSym.typeRange = typeRange;
        newSym.arrayRange = arrayRange;
        return newSym;
    }
    
    public void setParamList(ArrayList<String> paramList)
    {
        paramTypeList = paramList;
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

