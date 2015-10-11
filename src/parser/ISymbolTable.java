package parser;

public interface ISymbolTable
{
    public ISymbol getSymbol(String name);
    public ISymbol[] getAllSymbols();
    public boolean addSymbol(ISymbol sym);
}
