package parser.verilog;

import java.util.ArrayList;

import common.MyDebug;

public class SymbolParser implements VerilogTokenConstants, VerilogASTConstants
{
    /**
     * parse variable list kind symbols(parameter, input, output, inout, reg, time, 
     *     integer, real, net, defparam, event)
     * @param symTab current symbol table
     * @param node current node
     * @param kind symbol kind, must be port or generic
     */
    public static void parseVariableKind(ASTNode node, VerilogParser parser) {
        if(node == null) {
            MyDebug.printFileLine("parseVariableKind: null parameter!");
            return;
        }
        
        Symbol sym = new Symbol();
        sym.kind = node.getFirstToken().kind;
        
        ASTNode cNode = null;
        if((cNode = (ASTNode)node.getChildById(ASTRANGE)) != null) {
            sym.setRangeByNode(cNode);
        }
        
        if((cNode = (ASTNode)node.getChildById(ASTLIST_OF_VARIABLES)) != null) {
            parseList_of_variables(cNode, parser, sym);
        }else if((cNode = (ASTNode)node.getChildById(ASTLIST_OF_PARAM_ASSIGNMENTS)) != null) {
            parseList_of_param_assignments(cNode, parser, sym);
        }else if((cNode = (ASTNode)node.getChildById(ASTLIST_OF_REGISTER_VARIABLES)) != null) {
            parseList_of_register_variables(cNode, parser, sym);
        }else if(sym.kind == EVENT) {
            parseList_of_variables(node, parser, sym);
        }else {
            
        }
    }
    
    /**
     * parse subprogram kind symbols(task, function)
     * @param symTab current symbol table
     * @param node current node
     * @param kind symbol kind, must be port or generic
     */
    public static void parseSubProgramKind(ASTNode node, VerilogParser parser) {
        if(node == null) {
            MyDebug.printFileLine("parseSubProgramKind: null parameter!");
            return;
        }
        
        Symbol sym = new Symbol();
        sym.kind = node.getFirstToken().kind;
        
        ASTNode cNode = null;
        if((cNode = (ASTNode)node.getChildById(ASTRANGE)) != null) {
            sym.setRangeByNode(cNode);
            sym.name = ((ASTNode)node.getChild(1)).firstTokenImage();
        }else {
            sym.name = ((ASTNode)node.getChild(0)).firstTokenImage();
        }
        
        for(int i = 0; i < node.getChildrenNum(); i++) {
            cNode = (ASTNode)node.getChild(i);
            if(cNode.getId() != ASTTF_DECLARATION) {
                continue;
            }
            
            for(int j = 0; j < cNode.getChildrenNum(); j++) {
                ASTNode ccNode = (ASTNode)cNode.getChild(j);
                if(ccNode.first_token.kind == INPUT 
                        || ccNode.first_token.kind == OUTPUT
                        || ccNode.first_token.kind == INOUT) {
                    if(sym.paramTypeList == null) {
                        sym.paramTypeList = new ArrayList<String>();
                    }
                    for(int k = 0; k < node.getChildrenNum(); k++) {
                        sym.paramTypeList.add(((ASTNode)node.getChild(k)).firstTokenImage());
                    }
                }
            }
        }
        parser.curSymbolTable.addSymbol(sym);
    }
    
    private static void parseList_of_variables(ASTNode node, VerilogParser parser, Symbol sym) {
        for(int i = 0; i < node.getChildrenNum(); i++) {
            Symbol newSym = sym.clone();
            newSym.name = ((ASTNode)node.getChild(i)).firstTokenImage();
            parser.curSymbolTable.addSymbol(newSym);
        }
    }
    
    private static void parseList_of_param_assignments(ASTNode node, VerilogParser parser, 
                            Symbol sym) {
        for(int i = 0; i < node.getChildrenNum(); i++) {
            Symbol newSym = sym.clone();
            newSym.name = ((ASTNode)node.getChild(i).getChild(0)).firstTokenImage();
            newSym.value = ((ASTNode)node.getChild(i).getChild(1)).firstTokenImage();
            parser.curSymbolTable.addSymbol(newSym);
        }
    }
    
    private static void parseList_of_register_variables(ASTNode node, VerilogParser parser, 
                            Symbol sym) {
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i); // register_variable
            Symbol newSym = sym.clone();
            if(c.getChildById(ASTRANGE) != null) {
                newSym.setArrayRange(c.getChildById(ASTRANGE));
            }
            newSym.name = ((ASTNode)c.getChild(0)).firstTokenImage();
            parser.curSymbolTable.addSymbol(newSym);
        }
    }
}

