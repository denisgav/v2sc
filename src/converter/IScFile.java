package converter;

public interface IScFile
{
    public String getInclude();
    public String getDeclaration();
    public String getImplements();
    public void getIndividualString(StringBuffer strInclude, 
            StringBuffer strDeclaration, 
            StringBuffer strImplements);
}
