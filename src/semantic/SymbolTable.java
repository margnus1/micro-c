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

        Map initVarScope = new HashMap<String,Type>();

        varTable.add(initVarScope);

    }
    public void enterScope(){
        //add a new set to the linked list
        Map newVarScope = new HashMap<String,Type>();
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
        return null;

    }

    public FunctionType findFunction(String name, SimpleNode node){
        if(funcTable.containsKey(name)){
            return funcTable.get(name);
        }

        //need an exception?
        return null;
    }

    public boolean addVariable(String name, SimpleNode node){
        if(checkScope(name)){
            return false;
            //maybe throw exception
        }

        Map currentScope = varTable.get(varTable.size()-1);

        currentScope.put(name, new Type(node));


        return true;
    }

    public boolean addFunction(String name, SimpleNode node){
        if(funcTable.containsKey(name)){
            return false;
            //throw exception
        }

        funcTable.put(name, new FunctionType(node));
        return true;
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
}
