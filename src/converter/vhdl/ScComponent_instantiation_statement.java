package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> component_instantiation_statement ::=
 *   <dd> <i>instantiation_</i>label :
 *   <ul> instantiated_unit
 *   <ul> [ generic_map_aspect ]
 *   <br> [ port_map_aspect ] ; </ul></ul>
 */
class ScComponent_instantiation_statement extends ScCommonIdentifier implements IScStatementBlock {
    ScInstantiated_unit instantiated_unit = null;
    ScGeneric_map_aspect generic_map = null;
    ScPort_map_aspect port_map = null;
    
    String processName = "process";
    public ScComponent_instantiation_statement(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTCOMPONENT_INSTANTIATION_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTINSTANTIATED_UNIT:
                instantiated_unit = new ScInstantiated_unit(c);
                break;
            case ASTGENERIC_MAP_ASPECT:
                generic_map = new ScGeneric_map_aspect(c);
                break;
            case ASTPORT_MAP_ASPECT:
                port_map = new ScPort_map_aspect(c);
                break;
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                break;
            default:
                break;
            }
        }
        if(identifier.isEmpty())
            identifier = String.format("line%d", node.getFirstToken().beginLine);
        processName = addMethodName("process_comp_" + identifier);
    }

    private String getName() {
        return processName;
    }
    
    private String getSpec(boolean individual) {
        String ret = intent() + "void ";
        if(individual)
            ret += className + "::";
        ret += getName() + "(";
        if(param != null)
            ret += "int " + param;
        else
            ret += "void";
        ret += ")";
        return ret;
    }
    
    public String scString() {
        return "";
    }

    @Override
    public String getDeclaration() {
        String ret = "";
        //if(port_map != null) {
            String name = instantiated_unit.name.scString();
            ret += intent() + name;
            if(generic_map != null) {
                ret += "<" + generic_map.mapString(name) + ">";
            }
            name = "comp_" + name;
            ret += " " + name + "(\"" + name + "\");" + System.lineSeparator();
        //}
        ret += getSpec(false) + ";";
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = getSpec(curIndividual) + System.lineSeparator();
        ret += startIntentBraceBlock();
        if(port_map != null) {
            String name = instantiated_unit.name.scString();
            ret += port_map.mapString(name, "comp_" + name);
        }
        ret += endIntentBraceBlock();
        return ret;
    }

    @Override
    public String getInitCode()
    {
     // just call it
        String ret = intent() + getName() + "(";
        if(param != null)
            ret += param;
        ret += ");";
        return ret;
    }
}
