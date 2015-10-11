package parser.verilog;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import parser.INameObject;

/**
 * no reduplicated elements ArrayList
 */
public class VerilogArrayList<E extends INameObject> extends ArrayList<E>
{
    private static final long serialVersionUID = 6099154330118133178L;
    
    @Override
    public boolean add(E e)
    {
        if(e == null) {
            return false;
        }

        for(int i = 0; i < size(); i++) {
            if(e.equals(get(i))) {
                return false;
            }
        }
        return super.add(e);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        if(c == null) {
            return false;
        }
        
        Object[] eArray = c.toArray();

        for(int i = 0; i < eArray.length; i++) {
            add((E)eArray[i]);
        }
        return true;
    }
    
    public boolean addAll(E[] c)
    {
        if(c == null) {
            return false;
        }
        for(int i = 0; i < c.length; i++) {
            add(c[i]);
        }
        return true;
    }
    
    /**
     * get all elements which's name equals to specified name 
     */
    @SuppressWarnings("unchecked")
    public E[] get(String name)
    {
        if(name == null || name.isEmpty()) {
            return null;
        }

        ArrayList<E> eles = new ArrayList<E>();
        
        for(int i = 0; i < size(); i++) {
            if(name.equals(get(i).getName())) {
                eles.add(get(i));
            }
        }
        if(eles.size() == 0)
            return null;
        return eles.toArray((E[])Array.newInstance(eles.get(0).getClass(), eles.size()));
    }
}
