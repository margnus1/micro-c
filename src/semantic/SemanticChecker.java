package semantic;

import parser.*;
import java.util.*;

/**
 * Created by Doris on 14-2-13.
 */
public class SemanticChecker {
    public static void start(SimpleNode root){
        SymbolTable st = new SymbolTable();

        for(int i=0; i < root.jjtGetNumChildren(); i++){
            SimpleNode child = root.jjtGetChild(i);
            switch (child.getId()){
                case UcParseTreeConstants.JJTFUNCTIONDEFINITION:
                    checkFuncDef(child,st);
                    break;
                case UcParseTreeConstants.JJTFUNCTIONDECLARATION:
                    break;
                case UcParseTreeConstants.JJTVARIABLEDECLARATION:
                    break;
            }
        }

    }


    public static void checkFuncDef(SimpleNode funcDef, SymbolTable st){

        //get the second child, should be identifier
        SimpleNode id = funcDef.jjtGetChild(1);

        //get the name of the function
        String name = (String)id.jjtGetValue();

        //insert into symbol table
        st.addFunction(name,funcDef);

        //check FunctionParameters, the third child
        checkFuncParams(funcDef.jjtGetChild(2),st);

        //check CompoundStatement, the 4th child
        checkCompStmt(funcDef.jjtGetChild(3),st);

    }

    public static void checkFuncParams(SimpleNode funcParams, SymbolTable st){
        //only contains VariableDeclaration-s

        for(int i=0; i< funcParams.jjtGetNumChildren()-1; i++){

            SimpleNode varDec = funcParams.jjtGetChild(i);
            checkVarDec(varDec, st);
        }
    }

    public static void checkCompStmt(SimpleNode compStmt, SymbolTable st){

    }

    public static void checkVarDec(SimpleNode varDec, SymbolTable st){

    }

}
