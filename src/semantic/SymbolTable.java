package semantic;

import java.util.*;
import parser.*;

/**
 * Created by Doris on 14-2-12.
 */
public class SymbolTable {
    private List<Map<String,Type>> varTable;
    private Map<String,FunctionType> funcTable;

    public SymbolTable(){
        varTable = new ArrayList<Map<String,Type>>();

        funcTable = new HashMap<String,FunctionType>();

        Map<String,Type> initVarScope = new HashMap<String,Type>();

        varTable.add(initVarScope);

    }
    public void enterScope(){
        //add a new set to the linked list
        Map<String,Type> newVarScope = new HashMap<String,Type>();
        varTable.add(newVarScope);
        System.out.println("Enter Scope");
    }

    public Type findVariable(String name, SimpleNode node){
        int lastIdx = varTable.size()-1;
        for(int i = lastIdx; i>=0; i--){
            Map<String,Type> currentScope = varTable.get(i);
            if(currentScope.containsKey(name)){
                return currentScope.get(name);
            }
        }

        //need throw an exception VariableCannotBeFound?
        throw new SemanticError("Variable Definition Cannot Be Found", node);

    }

    public FunctionType findFunction(String name, SimpleNode node){
        if(funcTable.containsKey(name)){
            return funcTable.get(name);
        }

        throw new SemanticError("Function Definition Cannot Be Found", node);
    }

    public boolean addVariable(String name, SimpleNode node){
        if(checkScope(name)){
            throw new SemanticError("Redeclaration of variable",node);
        }

        Map<String,Type> currentScope = varTable.get(varTable.size()-1);

        currentScope.put(name, new Type(node));


        return true;
    }

    public void addFunction(String name, SimpleNode node){
        if(funcTable.containsKey(name)){
            throw new SemanticError("Function with the same name already exists", node);
        }

        funcTable.put(name, new FunctionType(node));
    }
    
    public boolean checkScope(String name){
        //check whether the symbol has already existed

        if(funcTable.containsKey(name)){
            return true;
        }


        for(Map m : varTable){
            if(m.containsKey(name)){
                return true;
            }
        }

        return false;
    }

    public void exitScope(){
        //remove the set at the end of the linked list
        varTable.remove(varTable.size()-1);
        System.out.println("Exit Scope");
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
