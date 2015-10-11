package converter.vhdl;

import java.util.ArrayList;

import converter.IScFile;
import converter.IncludePath;
import parser.IParser;
import parser.vhdl.ASTNode;
import parser.vhdl.VhdlArrayList;


/**
 * <dl> design_file ::=
 *   <dd> design_unit { design_unit }
 */
class ScDesign_file extends ScVhdl implements IScFile {
    ArrayList<ScDesign_unit> design_units = new ArrayList<ScDesign_unit>();
    public ScDesign_file(IParser parser) {
        this(parser, false);
    }
    
    /**
     * convert dividually: in order to decrease memory, <br>
     * when one standalone <b>entity</b> or <b>package</b> can be converted,<br>
     * it will be converted immediately and then it's memory will be free.<br>
     * <br>
     * "can be converted" means that:
     * <li> <b>entity</b> meets all of it's architecture body & configuration
     * <li> <b>package</b> meets it's package body
     */
    boolean convDividual = false;
    public ScDesign_file(IParser parser, boolean dividual) {
        super(parser);
        convDividual = dividual;
        units = new ArrayList<ScCommonIdentifier>();
        assert(curNode.getId() == ASTDESIGN_FILE);
        if(dividual)
            return; // convert dividually, parser later
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            assert(c.getId() == ASTDESIGN_UNIT);
            ScDesign_unit dunit = new ScDesign_unit(c);
            design_units.add(dunit);
        }
    }
    
    /**
     * can design unit be converted ? return it's index
     */
    protected int getIndexOfConverting() {
        /*int ret = -1;
        
        for(int i = 0; i < units.size(); i++) {
            ScCommonIdentifier ident = units.get(i);
            if(ident instanceof ScPackage_declaration
                && ((ScPackage_declaration)ident).body != null) {
                    ret = i;
                    break;
            }else if(ident instanceof ScEntity_declaration
                    && ((ScEntity_declaration)ident).body != null) {
                ret = i;
                break;
            }
        }*/
        if(units.size() > 1)    //TODO if there more than 1 units, convert the first
            return 0;
        return -1;
    }
    
    /**
     * get design_unit which contains entity or package
     */
    protected ScDesign_unit getConvertDesignUnit(ScCommonIdentifier ident)
    {
        ScDesign_unit ret = null;
        for(int i = 0; i < design_units.size(); i++) {
            ScDesign_unit dunit = design_units.get(i);
            ScVhdl u = dunit.library_unit.unit;
            if((u instanceof ScPrimary_unit && ((ScPrimary_unit)u).declaration == ident))
            {
                ret = design_units.remove(i);
                break;
            }
        }
        return ret;
    }
    
    /**
     * remove unit
     */
    protected void removeUnit(int index) {
        if(index < 0)
            return;
        ScCommonIdentifier ident = units.get(index);
        ScVhdl body = null;
        if(ident instanceof ScPackage_declaration) {
            body = ((ScPackage_declaration)ident).body;
        }else if(ident instanceof ScEntity_declaration) {
            body = ((ScEntity_declaration)ident).body;
        }
        
        // remove units from list
        units.remove(index);
        
        for(int i = 0; i < design_units.size(); i++) {
            ScDesign_unit dunit = design_units.get(i);
            ScVhdl u = dunit.library_unit.unit;
            if((u instanceof ScPrimary_unit && ((ScPrimary_unit)u).declaration == ident)
                    || (u instanceof ScSecondary_unit && ((ScSecondary_unit)u).item == body))
            {
                design_units.remove(i--);
            }
        }
    }
    
    public void getIndividualString(StringBuffer strInclude, 
            StringBuffer strDeclaration, 
            StringBuffer strImplements) {
        if(!convDividual) {
            strInclude.append(getInclude());
            strDeclaration.append(getDeclaration());
            strImplements.append(getImplements());
        }else {
            VhdlArrayList<IncludePath> list = new VhdlArrayList<IncludePath>();
            for(int i = 0; i < curNode.getChildrenNum(); i++) {
                ASTNode c = (ASTNode)curNode.getChild(i);
                assert(c.getId() == ASTDESIGN_UNIT);
                ScDesign_unit dunit = new ScDesign_unit(c);
                design_units.add(dunit);
                
                int index = getIndexOfConverting();
                if(i == curNode.getChildrenNum() - 1)
                    index = 0;  // always convert the last one
                if(index >= 0) {
                    ScDesign_unit myunit = getConvertDesignUnit(units.get(index));
                    IncludePath[] paths = myunit.getInclude();
                    list.addAll(paths);
                    String tmp = myunit.getDeclaration();
                    strDeclaration.append(addLF(tmp));
                    
                    tmp = myunit.getImplements();
                    strImplements.append(addLF(tmp));
                    removeUnit(index);
                }
                System.gc();
            }
            
            for(int i = 0; i < list.size(); i++) {
                strInclude.append(list.get(i));
                if(i < list.size() - 1) {
                    strInclude.append(System.lineSeparator());
                }
            }
        }
    }

    public String scString() {
        String ret = System.lineSeparator() + "#include <systemc.h>" + System.lineSeparator();
        for(int i = 0; i < design_units.size(); i++) {
            ret += design_units.get(i).toString();
            if(i < design_units.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        String ret = "";
        for(int i = 0; i < design_units.size(); i++) {
            ret += addLF(design_units.get(i).getDeclaration());
        }
        return ret;
    }

    @Override
    public String getImplements()
    {
        String ret = "";
        for(int i = 0; i < design_units.size(); i++) {
            ret += addLF(design_units.get(i).getImplements());
        }
        return ret;
    }

    @Override
    public String getInclude()
    {
        String ret = "";
        ret += addPrevComment();
        ret += System.lineSeparator() +  "#include <systemc.h>" + System.lineSeparator();
        VhdlArrayList<IncludePath> list = new VhdlArrayList<IncludePath>();
        for(int i = 0; i < design_units.size(); i++) {
            IncludePath[] paths = design_units.get(i).getInclude();
            list.addAll(paths);
        }
        
        for(int i = 0; i < list.size(); i++) {
            ret += list.get(i);
            if(i < list.size() - 1) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }
}

