package converter.vhdl;

import java.util.ArrayList;

import common.MyDebug;

import parser.vhdl.ASTNode;
import parser.IASTNode;
import parser.ISymbol;
import parser.vhdl.Symbol;
import parser.vhdl.SymbolTable;


/**
 * <dl> aggregate ::=
 *   <dd> ( element_association { , element_association } )
 */
class ScAggregate extends ScVhdl {
    ArrayList<ScElement_association> elementList = new ArrayList<ScElement_association>();
    public ScAggregate(ASTNode node) {
        super(node);
        assert(node.getId() == ASTAGGREGATE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            IASTNode c = node.getChild(i);
            ScElement_association n = new ScElement_association((ASTNode)c);
            elementList.add(n);
        }
    }
    
    public int getBitWidth() {
        int ret = 0;
        for(int i = 0; i < elementList.size(); i++) {
            ret += elementList.get(i).getBitWidth();
        }
        return ret;
    }
    
    public SymbolTable getTargetTypeSymbolTable() {
        SymbolTable ret = null;
        ASTNode node = null;
        ScCommonDeclaration cd = null;
        if((node = curNode.getAncestor(ASTCONSTANT_DECLARATION)) != null) {
            cd = new ScConstant_declaration(node);
        }else if((node = curNode.getAncestor(ASTSIGNAL_DECLARATION)) != null) {
            cd = new ScSignal_declaration(node);
        }else if((node = curNode.getAncestor(ASTVARIABLE_DECLARATION)) != null) {
            cd = new ScVariable_declaration(node);
        }
        
        if(cd != null) {
            Symbol sym = (Symbol)parser.getSymbol(node, cd.idList.items.get(0).identifier);
            if(sym == null) { return null; }
            ret = curNode.getSymbolTable().getTableOfSymbol(sym.type); //TODO: only tow level here
            if(ret != null) {
                ret = ret.getChildTable(sym.type);
            }
        }
        
        if(ret != null) {
            ISymbol[] syms = ret.getAllSymbols();
            if(syms == null || syms.length == 0) {
                ret = null; // no symbols in this table
            }
        }
        
        return ret;
    }
    
    protected void setLogic(boolean logic) {
        super.setLogic(logic);
        if(elementList.size() < 2)
            elementList.get(0).setLogic(logic);
    }

    public String scString() {
        String[] typeRange = getTargetRange();
        String[] arrayRange = getTargetArrayRange();
        //if(getTargetTypeSymbolTable() != null) {
        //      MyDebug.printFileLine();
        //}
        SymbolTable recordTable = getTargetTypeSymbolTable();
        boolean isArray = (arrayRange != null);
        
        
        String ret = "";
        if(isArray || recordTable != null)
            ret += "{";
        else if(elementList.size() > 1)
            ret += "(";
        
        if(isArray && elementList.get(0).choices != null) {
            String othersValue = "";
            int maxId = -1;
            for(int i = 0; i < elementList.size(); i++) {
                ScElement_association ele = elementList.get(i);
                if(ele.choices.isOthers()) {
                    othersValue = getReplaceValue(ele.expression.scString());
                }else {
                    try {
                        int v = getIntValue(ele.choices.items.get(0).toString());
                        if(v > maxId)
                            maxId = v;
                    }catch(NumberFormatException e) {
                        maxId = -1;
                        break;
                    }
                }
            }
            
            if(maxId >= 0) {
                for(int i = 0; i <= maxId; i++) {
                    int j = 0;
                    // find index in choices
                    for(j = 0; j < elementList.size(); j++) {
                        ScElement_association ele = elementList.get(j);
                        if(!ele.choices.isOthers()) {
                            try {
                                int v = getIntValue(ele.choices.items.get(0).toString());
                                if(v == i) {
                                    ret += getReplaceValue(ele.expression.scString());
                                    break;
                                }
                            }catch(NumberFormatException e) {
                                MyDebug.printFileLine("internal error!");
                            }
                        }
                    }
                    
                    // not found
                    if(j >= elementList.size()) {
                        ret += othersValue;
                    }
                    ret += ", ";
                }
                
                if(!othersValue.isEmpty()) {
                    ret += "others => " + othersValue;
                }
            }else {
                for(int i = 0; i < elementList.size(); i++) {
                    ret += elementList.get(i).orgString();
                    if(i < elementList.size() - 1)
                        ret += ", ";
                }
            }
                
        }else {
            int max = 1;
            if(isArray) {
                max = getWidth(arrayRange[0], arrayRange[2]);
            }else if(typeRange != null) {
                max = getWidth(typeRange[0], typeRange[2]);
            }
            
            if(elementList.size() == 1 && elementList.get(0).choices.isOthers())
                ret += elementList.get(0).toBitString(1, isArray);
            else {
                int num = 0;
                Symbol[] recSyms = null;
                if(recordTable != null)
                    recSyms = (Symbol[])recordTable.getAllSymbols();
                for(int i = 0; i < elementList.size(); i++) {
                    int width = max-num;
                    if(recordTable != null && recSyms[i].typeRange != null) {
                        width = getWidth(recSyms[i].typeRange[0], recSyms[i].typeRange[2]);
                    }
                    ret += elementList.get(i).toBitString(width, isArray);
                    num += elementList.get(i).getBitWidth();
                    if(i < elementList.size() - 1) {
                        ret += ", ";
                    }
                }
            }            
        }
        

        
        if(isArray || recordTable != null)
            ret += "}";
        else if(elementList.size() > 1)
            ret += ")";
        return ret;
    }
}
