package semantic;

import parser.*;

/**
 * Created by Doris on 14-2-13.
 */
public class SemanticChecker {
    public SemanticChecker() {};
    SymbolTable st = new SymbolTable();
    FunctionType currentFunctionContext = null;

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
        currentFunctionContext = st.addFunctionDefinition(name, funcDef);

        //create a new Scope
        st.enterScope(funcDef.jjtGetChild(3));

        //check FunctionParameters, the third child
        checkFuncParams(funcDef.jjtGetChild(2));

        //check CompoundStatement, the 4th child
        checkFuncCompStmt(funcDef.jjtGetChild(3));

        //exit the scope
        st.exitScope();
        currentFunctionContext = null;
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
        assert currentFunctionContext != null;
        if (currentFunctionContext.getReturnType().isVoid()) {
            if (retStmt.jjtGetNumChildren() != 0)
                throw new SemanticError("Value returned from void function.",
                        retStmt, currentFunctionContext.getNode());
        } else {
            if (retStmt.jjtGetNumChildren() != 1)
                throw new SemanticError("Return from non-void function is missing a value.",
                        retStmt, currentFunctionContext.getNode());
            checkExpr(retStmt.jjtGetChild(0))
                    .assertConvertibleTo(currentFunctionContext.getReturnType(), retStmt);
        }
    }

    private Type checkExpr(SimpleNode expr){
        switch (expr.getId()) {
            case UcParseTreeConstants.JJTASSIGNMENT:
                return checkAssignment(expr);
            case UcParseTreeConstants.JJTARRAYLOOKUP:
                return checkArrayLookup(expr);
            case UcParseTreeConstants.JJTIDENTIFIER:
                return st.findVariable(expr);
            case UcParseTreeConstants.JJTBINARY:
                Type lhs = checkExpr(expr.jjtGetChild(0));
                Type rhs = checkExpr(expr.jjtGetChild(1));
                lhs.assertArithmetic(expr);
                return lhs.unify(rhs, expr);
            case UcParseTreeConstants.JJTUNARYEXPR:
                Type t = checkExpr(expr.jjtGetChild(0));
                t.assertArithmetic(expr);
                return t;
            case UcParseTreeConstants.JJTFUNCTIONCALL:
                return checkFuncCall(expr);
            case UcParseTreeConstants.JJTINTEGERLITERAL:
                return new Type(expr);
            default:
                throw new RuntimeException("Unexpected " + expr + " in AST. Expected expression.");
        }
        //expression can be an identifier
    }

    private Type checkFuncCall(SimpleNode expr) {
        SimpleNode func = expr.jjtGetChild(0);
        if (func.getId() != UcParseTreeConstants.JJTIDENTIFIER)
            throw new SemanticError("A function must be an identifier.", func);
        FunctionType ft = st.findFunction((String)func.jjtGetValue(), func);

        SimpleNode args = expr.jjtGetChild(1);
        if (args.jjtGetNumChildren() != ft.getArgumentTypes().length)
            throw new SemanticError("Wrong number of arguments in call to function " + func.jjtGetValue() + ".",
                    expr, ft.getNode());
        for (int i = 0; i < args.jjtGetNumChildren(); i++)
            checkExpr(args.jjtGetChild(i)).assertConvertibleTo(ft.getArgumentTypes()[i], args.jjtGetChild(i));

        return new Type(ft.getReturnType(), expr);
    }

    private Type checkAssignment(SimpleNode expr) {
        Type lhs = checkAssignable(expr.jjtGetChild(0));
        Type rhs = checkExpr(expr.jjtGetChild(1));
        rhs.assertConvertibleTo(lhs, expr);
        return lhs;
    }

    private Type checkAssignable(SimpleNode simpleNode) {
        switch (simpleNode.getId()) {
            case UcParseTreeConstants.JJTIDENTIFIER:
                Type t = st.findVariable(simpleNode);
                if (t.isVector()) throw new SemanticError("You cannot assign to a vector.", simpleNode, t.getExpr());
                return t;
            case UcParseTreeConstants.JJTARRAYLOOKUP:
                return checkArrayLookup(simpleNode);
            default:
                throw new SemanticError("Left hand side of assignment must be an identifier or array lookup.", simpleNode);
        }
    }

    private Type checkArrayLookup(SimpleNode lookup) {
        Type arrT = st.findVariable(lookup.jjtGetChild(0));
        if (!arrT.isIndexable()) throw new SemanticError(arrT + " cannot be indexed.", lookup, arrT.getExpr());
        Type ixT = checkExpr(lookup.jjtGetChild(1));
        ixT.assertArithmetic(lookup);
        return arrT.getElementType();
    }

}
