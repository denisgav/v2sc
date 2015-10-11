package parser.vhdl;

import java.util.ArrayList;

import parser.IASTNode;
import parser.INameObject;
import parser.Token;

public class ASTNode implements IASTNode
{
    private String name = "";
    protected IASTNode parent;
    protected ArrayList<IASTNode> children = new ArrayList<IASTNode>(1);
    protected int id;
    protected Token first_token = null;
    protected Token last_token = null;
    protected SymbolTable symTab = null;
    
    public ASTNode(IASTNode p, int id) {
        parent = p;
        this.id = id;
        if(p != null) {
            p.addChild(this);
        }
    }
    
    public void addChild(IASTNode n) {
        children.add(n);
    }

    public IASTNode getChild(int i) {
        if(i < children.size())
            return children.get(i);
        return null;
    }

    public int getChildrenNum() {
        return children.size();
    }

    public int getId() {
        return id;
    }

    public IASTNode getParent() {
        return parent;
    }

    public void setParent(IASTNode p) {
        parent = p;
    }
    
    public void setFirstToken(Token token) {
        first_token = token;
    }
    
    public void setLastToken(Token token) {
        last_token = token;  
    }
    
    public Token getFirstToken() {
        return first_token;
    }
    
    public Token getLastToken() {
        return last_token;
    }
    
    public String toString() {
        return VhdlASTConstants.ASTNodeName[id];
    }
    
    public String firstTokenImage() {
        if(first_token != null) {
            return first_token.image;
        }else {
            return "invalid_image";
        }
    }

    public IASTNode getChildById(int id) {
        for(int i = 0; i < children.size(); i++) {
            IASTNode child = children.get(i);
            if(child.getId() == id) {
                return child; 
            }
        }
        return null;
    }
    
    /**
     * search descendant recursive to find the first specified ASTNode
     */
    public IASTNode getDescendant(int id) {
        IASTNode ret = null;
        for(int i = 0; i < children.size(); i++) {
            ASTNode child = (ASTNode)children.get(i);
            if(child.getId() == id) {
                ret = child;
                break;
            }
            ret = child.getDescendant(id);
            if(ret != null) { break; }
        }
        return ret;
    }
    
    /**
     * search descendant recursive to find the first specified ASTNode
     */
    public IASTNode getDescendant(int id, String name) {
        IASTNode ret = null;
        for(int i = 0; i < children.size(); i++) {
            ASTNode child = (ASTNode)children.get(i);
            if(child.getId() == id && name.equalsIgnoreCase(child.getName())) {
                ret = child;
                break;
            }
            ret = child.getDescendant(id, name);
            if(ret != null) { break; }
        }
        return ret;
    }
    
    /**
     * whether this node is descendant of specified type node
     */
    public boolean isDescendantOf(int id) {
        return (getAncestor(id) != null);
    }
    
    /**
     * get specified type ancestor node
     */
    public ASTNode getAncestor(int id) {
        ASTNode ret = null;
        IASTNode pNode = parent;
        while(pNode != null) {
            if(pNode.getId() == id) {
                ret = (ASTNode)pNode;
                break;
            }
            pNode = pNode.getParent();
        }
        return ret;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        if(name.isEmpty()) {
            // default: node name is its first identifier child node
            // package, entity, component, architecture, etc.
            IASTNode child = getChildById(VhdlASTConstants.ASTIDENTIFIER);
            if(child != null) {
                name = ((ASTNode)child).firstTokenImage();
            }
            
            // function/procedure
            if(name.isEmpty()) {
                child = getDescendant(VhdlASTConstants.ASTDESIGNATOR);
                if(child != null) {
                    name = ((ASTNode)child).firstTokenImage();
                }
            }
            
            if(name.isEmpty()) {
                child = getDescendant(VhdlASTConstants.ASTIDENTIFIER);
                if(child != null) {
                    name = ((ASTNode)child).firstTokenImage();
                }
            }
            
            if(name.isEmpty() && first_token.kind == VhdlTokenConstants.identifier) {
                name = first_token.image;
            }
            
            if(name.isEmpty()) {
                name = String.format("%s_line%d", first_token.image, 
                                first_token.beginLine);
            }
        }

        return name;
    }

    @Override
    public boolean equals(INameObject other) {
        return (other == this);
    }
    
    public SymbolTable getSymbolTable() {
        return symTab;
    }
    
    public void setSymbolTable(SymbolTable tab) {
        symTab = tab;
    }
}

class ASTtoken extends ASTNode
{
    String image = "";
    public ASTtoken(IASTNode p, String image) {
        super(p, VhdlASTConstants.ASTVOID);
        this.image = image;
    }
    
    public String toString() {
        return image;
    }
    public String firstTokenImage() {
        return image;
    }
}

