package converter;

import parser.INameObject;

public class IncludePath implements INameObject
{
    String path = "";
    public IncludePath(String p)
    {
        path = p;
    }
    
    @Override
    public boolean equals(INameObject other)
    {
        return path.equalsIgnoreCase(other.getName());
    }

    @Override
    public String getName()
    {
        return path;
    }

    @Override
    public void setName(String name)
    {
        path = name;
    }
    
    @Override
    public String toString()
    {
        return path;
    }
}
