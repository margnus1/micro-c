package semantic;

import java.util.*;
import parser.*;

/**
 * Created by Doris on 14-2-12.
 */

//local variable can disable(?) the visibility with the same name of function
public class SymbolTable {
    private List<Map<String,Type>> varTable;
    private Map<String,FunctionType> funcTable;

    public SymbolTable(){
        varTable = new ArrayList<Map<String,Type>>();

        funcTable = new HashMap<String,FunctionType>();

        Map<String,Type> initVarScope = new HashMap<String,Type>();

        varTable.add(initVarScope);
    }

    public void enterScope(SimpleNode compoundStmt){
        //add a new set to the linked list
        Map<String,Type> newVarScope = new HashMap<String,Type>();
        varTable.add(newVarScope);
        if(compoundStmt.jjtGetValue() != null)
            throw new RuntimeException("Non-null value of compound statement");
        compoundStmt.jjtSetValue(newVarScope);
    }

    public Type findVariable(SimpleNode node){
        assert node.getId() == UcParseTreeConstants.JJTIDENTIFIER;
        String name = (String)node.jjtGetValue();
        Type var = lookupVariable(name);
        if (var != null) return var;

        throw new SemanticError("Undefined variable.", node);
    }

    public FunctionType findFunction(SimpleNode node){
        assert node.getId() == UcParseTreeConstants.JJTIDENTIFIER;
        String name = (String)node.jjtGetValue();
        Type varT = lookupVariable(name);
        if(varT != null){
            throw new SemanticError("Function " + name + " is shadowed by a local variable.",
                    node, varT.getExpr());
        }

        if(funcTable.containsKey(name)){
            return funcTable.get(name);
        }

        throw new SemanticError("Undefined function.", node);
    }

    public Type addVariable(SimpleNode node){
        String name = getVarName(node);
        Map<String,Type> currentScope = varTable.get(varTable.size()-1);

        if(currentScope.containsKey(name)){
            throw new SemanticError("A variable with the same name was already declared in this scope.",
                    node, currentScope.get(name).getExpr());
        }
        if (varTable.size() == 1 && funcTable.containsKey(name)) {
            throw new SemanticError("Global variable name clashes with function name.",
                    node, funcTable.get(name).getNode());
        }

        Type varType = new Type(node);
        currentScope.put(name, varType);
        return varType;
    }

    private String getVarName(SimpleNode declaration) {
        assert (declaration.getId() == UcParseTreeConstants.JJTVARIABLEDECLARATION);
        // get the second child of variable declaration
        // it is either an Identifier or ArrayDeclarator
        SimpleNode secondChild = declaration.jjtGetChild(1);
        switch (secondChild.getId()){
            case UcParseTreeConstants.JJTIDENTIFIER:
                return (String)secondChild.jjtGetValue();
            case UcParseTreeConstants.JJTARRAYDECLARATOR:
                return (String)secondChild.jjtGetChild(0).jjtGetValue();
            default:
                throw new RuntimeException("Invalid node in AST.");
        }
    }

    public void addFunctionDeclaration(SimpleNode node){
        assert node.getId() == UcParseTreeConstants.JJTFUNCTIONDECLARATION
            || node.getId() == UcParseTreeConstants.JJTFUNCTIONDEFINITION;
        String name = (String)node.jjtGetChild(1).jjtGetValue();
        FunctionType type = new FunctionType(node);

        if(funcTable.containsKey(name)) {
            if (!funcTable.get(name).equals(type))
                throw new SemanticError("Declarations of function " + name + " have conflicting types.",
                        funcTable.get(name).getNode(), node);
        } else if (varTable.get(0).containsKey(name)) {
            throw new SemanticError("Global variable name clashes with function name.",
                    varTable.get(0).get(name).getExpr(), node);
        } else {
            funcTable.put(name, type);
        }
    }

    public FunctionType addFunctionDefinition(SimpleNode node) {
        assert node.getId() == UcParseTreeConstants.JJTFUNCTIONDEFINITION;
        String name = (String)node.jjtGetChild(1).jjtGetValue();
        assert(funcTable.containsKey(name));
        FunctionType declaration = funcTable.get(name);
        if (declaration.getDefinition() != null)
            throw new SemanticError("Function " + declaration + " defined multiple times.",
                    declaration.getDefinition(), node);
        declaration.setDefinition(node);
        return declaration;
    }

    /**
     * Looks up a variable by name, returning null if it's not found.
     */
    private Type lookupVariable(String name){
        int lastIdx = varTable.size()-1;
        for(int i = lastIdx; i>=0; i--){
            Map<String,Type> currentScope = varTable.get(i);
            if(currentScope.containsKey(name)){
                return currentScope.get(name);
            }
        }
        return null;
    }

    public void exitScope(){
        //remove the set at the end of the linked list
        varTable.remove(varTable.size()-1);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Map s : varTable){
            sb.append(s.toString());
            sb.append("\n");
        }
        sb.append(funcTable.toString()+"\n");
        return sb.toString();
    }
}
