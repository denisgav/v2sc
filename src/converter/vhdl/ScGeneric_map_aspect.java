package converter.vhdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import common.MyDebug;

import parser.vhdl.ASTNode;
import parser.vhdl.LibSymbolTable;
import parser.vhdl.LibraryManager;
import parser.vhdl.Symbol;
import parser.vhdl.SymbolTable;


/**
 * <dl> generic_map_aspect ::=
 *   <dd> <b>generic</b> <b>map</b> ( <i>generic_</i>association_list )
 */
class ScGeneric_map_aspect extends ScVhdl {
    ScAssociation_list association_list = null;
    public ScGeneric_map_aspect(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTGENERIC_MAP_ASPECT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTASSOCIATION_LIST:
                association_list = new ScAssociation_list(c);
                break;
            default:
                break;
            }
        }
    }
    
    /**
     * map to component
     * @param name: name of component
     */
    public String mapString(String name) {
        String ret = "";
        int i = 0;

        SymbolTable symTab = null;
        if(name.indexOf('.') > 0) {
            StringTokenizer tkn = new StringTokenizer(name, ".");
            String n0 = tkn.nextToken();
            String n1 = tkn.nextToken();
            if(n0.equalsIgnoreCase("work")) {
                n0 = LibraryManager.getInstance().findWorkLibrary(n1);
            }
            symTab = new LibSymbolTable(symTab, n0);
            symTab = new LibSymbolTable(symTab, n1);
            String n2 = "";
            if(tkn.hasMoreTokens()) {
                n2 = tkn.nextToken();
                symTab = new LibSymbolTable(symTab, n2);
            }
        }else {
            symTab = (SymbolTable)parser.getTableOfSymbol(curNode, name);
            if(symTab == null) {
                MyDebug.printFileLine("component not found:" + name);
                return ret;
            }
            symTab = symTab.getChildTable(name);
        }

        
        Symbol[] syms = symTab.getKindSymbols(GENERIC);
        if(syms == null || syms.length == 0) {
            MyDebug.printFileLine("no generic in component:" + name);
            return ret;
        }
        ArrayList<ScAssociation_element> elements = association_list.elements;
        
        HashMap<String, String> genMap = new HashMap<String, String>();
        int maxIndex = -1;
        for(i = 0; i < syms.length; i++) {
            String value = null;
            for(int j = 0; j < elements.size(); j++) {
                if(elements.get(j).formal_part != null) {
                    String genName = elements.get(j).formal_part.scString();
                    if(genName.equalsIgnoreCase(syms[i].name)) {
                        value = elements.get(j).actual_part.scString();
                        break;
                    }
                }else if(i < elements.size()) {
                    value = elements.get(i).actual_part.scString();
                    break;
                }
            }
            genMap.put(syms[i].name, value);
            if(value != null)
                maxIndex = i;
        }
        
        for(i = 0; i <= maxIndex; i++) {
            String value = genMap.get(syms[i].name);
            if(value == null)
                value = syms[i].value;
            ret += value;
            if(i < maxIndex)
                ret += ", ";
        }
        
/*        
        for(i = 0; i < elements.size(); i++) {
            if(elements.get(i).formal_part!= null) {
                String genName = elements.get(i).formal_part.scString();
                for(int j = i; j < syms.length; j++) {     // add omitted items
                    Symbol sym = syms[j];
                    if(sym.name.equalsIgnoreCase(genName)) {
                        break;
                    }
                    ret += sym.value;
                    if(j < syms.length - 1 && i < elements.size() - 1) {
                        ret += ", ";
                    }
                }
            }
            ret += elements.get(i).actual_part.scString();
            if(i < elements.size() - 1) {
                ret += ", ";
            }
        }
*/
        return ret;
    }

    public String scString() {
        return "";
    }
}
