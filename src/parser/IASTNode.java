package parser;

public interface IASTNode extends INameObject
{
    public void setParent(IASTNode n);
    public IASTNode getParent();
    public void addChild(IASTNode n);
    public IASTNode getChild(int i);
    public IASTNode getChildById(int id);
    int getChildrenNum();
    public int getId();
}
