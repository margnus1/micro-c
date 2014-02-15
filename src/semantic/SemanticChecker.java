package semantic;

import parser.*;

/**
 * Created by Doris on 14-2-13.
 */
public class SemanticChecker {
    public SemanticChecker() {};
    SymbolTable st = new SymbolTable();

    public void start(SimpleNode root){
        for(int i=0; i < root.jjtGetNumChildren(); i++){
            SimpleNode child = root.jjtGetChild(i);
            switch (child.getId()){
                case UcParseTreeConstants.JJTFUNCTIONDEFINITION:
                    checkFuncDecl(child);
                    checkFuncDef(child);
                    break;
                case UcParseTreeConstants.JJTFUNCTIONDECLARATION:
                    checkFuncDecl(child);
                    break;
                case UcParseTreeConstants.JJTVARIABLEDECLARATION:
                    st.addVariable(child);
                    break;
            }
        }
    }

    public void checkFuncDecl(SimpleNode funcDef) {
        //get the second child, should be an identifier
        SimpleNode id = funcDef.jjtGetChild(1);
        String name = (String)id.jjtGetValue();
        st.addFunctionDeclaration(name, funcDef);
    }

    public void checkFuncDef(SimpleNode funcDef){

        //get the second child, should be identifier
        SimpleNode id = funcDef.jjtGetChild(1);

        //get the name of the function
        String name = (String)id.jjtGetValue();

        //insert into symbol table
        st.addFunctionDefinition(name, funcDef);

        //create a new Scope
        st.enterScope(funcDef.jjtGetChild(3));

        //check FunctionParameters, the third child
        checkFuncParams(funcDef.jjtGetChild(2));

        //check CompoundStatement, the 4th child
        checkFuncCompStmt(funcDef.jjtGetChild(3));

        //exit the scope
        st.exitScope();



    }

    public void checkFuncParams(SimpleNode funcParams){
        //only contains VariableDeclaration-s

        for(int i=0; i< funcParams.jjtGetNumChildren(); i++){

            SimpleNode varDec = funcParams.jjtGetChild(i);
            st.addVariable(varDec);
        }
    }

    public void checkFuncCompStmt(SimpleNode compStmt){
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
            checkStmt(stmt);
        }

    }

    //question?????: how to guarantee access of an array doesn't exceed the total length

    //check the standalone Compound Statements
    public void checkCompStmt(SimpleNode compStmt){
        //need to add a new scope by ourselves

        //enter a new scope
        st.enterScope(compStmt);

        //reuse the checkFuncCompStmt
        checkFuncCompStmt(compStmt);

        //exit the scope
        st.exitScope();
    }

    private void checkStmt(SimpleNode stmt) {
        switch (stmt.getId()){
            case UcParseTreeConstants.JJTIFSTATEMENT:
                checkIfStmt(stmt, st);
                break;
            case UcParseTreeConstants.JJTEMPTYSTATEMENT:
                checkEmptyStmt(stmt);
                break;
            case UcParseTreeConstants.JJTWHILESTATEMENT:
                checkWhileStmt(stmt);
                break;
//            case UcParseTreeConstants.JJTCOMPOUNDSTATEMENT:
//                checkCompStmt(stmt,st);
//                break;
            case UcParseTreeConstants.JJTSIMPLECOMPOUNDSTATEMENT:
                //to be checked?: stmt can only have SimpleCompoundStatement
                checkSimpleCompStmt(stmt);
                break;
            case UcParseTreeConstants.JJTRETURNSTATEMENT:
                checkRetStmt(stmt);
                break;
            default:
                //should be expressions
                checkExpr(stmt);
        }
    }

    private void checkIfStmt(SimpleNode ifStmt, SymbolTable st){
        int numOfChild = ifStmt.jjtGetNumChildren();

        SimpleNode condition = ifStmt.jjtGetChild(0);
        checkExpr(condition);

        //regard the then & else block as SimpleCompoundStatement
        //!!find another construct named SimpleCompoundState
        //the only difference between SimpleCompStmt & CompStmt is SimpleCompStmt doesn't allow varDecs

        SimpleNode thenStmt = ifStmt.jjtGetChild(1);
        checkStmt(thenStmt);

        if(numOfChild==3){
            SimpleNode elseStmt = ifStmt.jjtGetChild(2);
            checkStmt(elseStmt);
        }
    }

    private void checkEmptyStmt(SimpleNode emptyStmt){
        //seems nothing to check
    }

    private void checkWhileStmt(SimpleNode whileStmt){

        SimpleNode condition = whileStmt.jjtGetChild(0);
        checkExpr(condition);

        SimpleNode innerStmt = whileStmt.jjtGetChild(1);
        checkSimpleCompStmt(innerStmt);

    }

    private void checkSimpleCompStmt(SimpleNode simpleCompStmt){
        //create a new scope
        st.enterScope(simpleCompStmt);

        for(int i =0; i < simpleCompStmt.jjtGetNumChildren(); i++){
            SimpleNode stmt = simpleCompStmt.jjtGetChild(i);
            checkStmt(stmt);
        }

        //exit the scope
        st.exitScope();
    }

    private void checkRetStmt(SimpleNode retStmt){

    }

    private void checkExpr(SimpleNode expr){
        //expression can be an identifier
    }

}
