package semantic;

import parser.*;

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
                    checkFuncDecl(child, st);
                    checkFuncDef(child, st);
                    break;
                case UcParseTreeConstants.JJTFUNCTIONDECLARATION:
                    checkFuncDecl(child, st);
                    break;
                case UcParseTreeConstants.JJTVARIABLEDECLARATION:
                    st.addVariable(child);
                    break;
            }
        }
    }

    public static void checkFuncDecl(SimpleNode funcDef, SymbolTable st) {
        //get the second child, should be an identifier
        SimpleNode id = funcDef.jjtGetChild(1);
        String name = (String)id.jjtGetValue();
        st.addFunctionDeclaration(name, funcDef);
    }

    public static void checkFuncDef(SimpleNode funcDef, SymbolTable st){

        //get the second child, should be identifier
        SimpleNode id = funcDef.jjtGetChild(1);

        //get the name of the function
        String name = (String)id.jjtGetValue();

        //insert into symbol table
        st.addFunctionDefinition(name, funcDef);

        //create a new Scope
        st.enterScope(funcDef.jjtGetChild(3));

        //check FunctionParameters, the third child
        checkFuncParams(funcDef.jjtGetChild(2), st);

        //check CompoundStatement, the 4th child
        checkFuncCompStmt(funcDef.jjtGetChild(3), st);

        //exit the scope
        st.exitScope();



    }

    public static void checkFuncParams(SimpleNode funcParams, SymbolTable st){
        //only contains VariableDeclaration-s

        for(int i=0; i< funcParams.jjtGetNumChildren(); i++){

            SimpleNode varDec = funcParams.jjtGetChild(i);
            st.addVariable(varDec);
        }
    }

    public static void checkFuncCompStmt(SimpleNode compStmt, SymbolTable st){
        //CompoundStatement within a FunctionDefinition
        //the new Scope is created by checkFuncDef
        //while the standalone CompoundStatement needs to created
        //its own Scope

        //first child, variableDeclarations
        SimpleNode varDecs = compStmt.jjtGetChild(0);

        //deal with each varDec
        for(int i=0; i< varDecs.jjtGetNumChildren(); i++){
            SimpleNode varDec = varDecs.jjtGetChild(i);
            st.addVariable(varDec);
        }


        //second child, statements
        SimpleNode stmts = compStmt.jjtGetChild(1);
        for(int i =0; i < stmts.jjtGetNumChildren(); i++){
            SimpleNode stmt = stmts.jjtGetChild(i);
            checkStmt(stmt, st);
        }

    }

    //question?????: how to guarantee access of an array doesn't exceed the total length

    //check the standalone Compound Statements
    public static void checkCompStmt(SimpleNode compStmt, SymbolTable st){
        //need to add a new scope by ourselves

        //enter a new scope
        st.enterScope(compStmt);

        //reuse the checkFuncCompStmt
        checkFuncCompStmt(compStmt,st);

        //exit the scope
        st.exitScope();
    }

    private static void checkStmt(SimpleNode stmt, SymbolTable st) {
        switch (stmt.getId()){
            case UcParseTreeConstants.JJTIFSTATEMENT:
                checkIfStmt(stmt, st);
                break;
            case UcParseTreeConstants.JJTEMPTYSTATEMENT:
                checkEmptyStmt(stmt, st);
                break;
            case UcParseTreeConstants.JJTWHILESTATEMENT:
                checkWhileStmt(stmt, st);
                break;
//            case UcParseTreeConstants.JJTCOMPOUNDSTATEMENT:
//                checkCompStmt(stmt,st);
//                break;
            case UcParseTreeConstants.JJTSIMPLECOMPOUNDSTATEMENT:
                //to be checked?: stmt can only have SimpleCompoundStatement
                checkSimpleCompStmt(stmt, st);
                break;
            case UcParseTreeConstants.JJTRETURNSTATEMENT:
                checkRetStmt(stmt,st);
                break;
            default:
                //should be expressions
                checkExpr(stmt,st);
        }
    }

    private static void checkIfStmt(SimpleNode ifStmt, SymbolTable st){
        int numOfChild = ifStmt.jjtGetNumChildren();

        SimpleNode condition = ifStmt.jjtGetChild(0);
        checkExpr(condition,st);

        //regard the then & else block as SimpleCompoundStatement
        //!!find another construct named SimpleCompoundState
        //the only difference between SimpleCompStmt & CompStmt is SimpleCompStmt doesn't allow varDecs

        SimpleNode thenStmt = ifStmt.jjtGetChild(1);
        checkStmt(thenStmt, st);

        if(numOfChild==3){
            SimpleNode elseStmt = ifStmt.jjtGetChild(2);
            checkStmt(elseStmt, st);
        }
    }

    private static void checkEmptyStmt(SimpleNode emptyStmt, SymbolTable st){
        //seems nothing to check
    }

    private static void checkWhileStmt(SimpleNode whileStmt, SymbolTable st){

        SimpleNode condition = whileStmt.jjtGetChild(0);
        checkExpr(condition,st);

        SimpleNode innerStmt = whileStmt.jjtGetChild(1);
        checkSimpleCompStmt(innerStmt,st);

    }

    private static void checkSimpleCompStmt(SimpleNode simpleCompStmt, SymbolTable st){
        //create a new scope
        st.enterScope(simpleCompStmt);

        for(int i =0; i < simpleCompStmt.jjtGetNumChildren(); i++){
            SimpleNode stmt = simpleCompStmt.jjtGetChild(i);
            checkStmt(stmt, st);
        }

        //exit the scope
        st.exitScope();
    }

    private static void checkRetStmt(SimpleNode retStmt, SymbolTable st){

    }

    private static void checkExpr(SimpleNode expr, SymbolTable st){
        //expression can be an identifier
    }

}
