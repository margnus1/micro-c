package semantic;

import parser.*;

import java.util.*;

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

    //do not know what to do with function declaration



    //check variable declaration within the function parameter list
    public static void checkFuncVarDec(SimpleNode varDec, SymbolTable st){
        //variable declaration in function definition int a[]
        //but normal variable declaration also allows a[5]

        //get the second child of variable declaration
        //it is either an identifier or arraydeclarator
        SimpleNode secondChild = varDec.jjtGetChild(1);
        switch (secondChild.getId()){
            case UcParseTreeConstants.JJTIDENTIFIER:
                //nothing to check, insert into symbol table
                String name = (String)secondChild.jjtGetValue();
                st.addVariable(name,varDec);
                break;
            case UcParseTreeConstants.JJTARRAYDECLARATOR:
                //get the number of children of array declarator
                int numChildOfArrayDec = secondChild.jjtGetNumChildren();
                if(numChildOfArrayDec == 1){
                    //declaration without IntegerLiteral, correct
                    String arrName = (String)secondChild.jjtGetChild(0).jjtGetValue();
                    st.addVariable(arrName,varDec);
                }else{
                    //throw an exception
                    throw new SemanticError("Illegal Function Definition",varDec);
                }
                break;
        }


    }

    //check variable declaration elsewhere
    public static void checkVarDec(SimpleNode varDec, SymbolTable st){
        //get the second child of variable declaration
        //it is either an identifier or arraydeclarator
        SimpleNode secondChild = varDec.jjtGetChild(1);
        switch (secondChild.getId()){
            case UcParseTreeConstants.JJTIDENTIFIER:
                //nothing to check, insert into symbol table
                String name = (String)secondChild.jjtGetValue();

                st.addVariable(name,varDec);
                break;
            case UcParseTreeConstants.JJTARRAYDECLARATOR:
                String arrName = (String)secondChild.jjtGetChild(0).jjtGetValue();
                st.addVariable(arrName,varDec);
                break;
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

        checkStmt(thenStmt, st);

        if(numOfChild==3){
            SimpleNode elseStmt = ifStmt.jjtGetChild(2);
            checkStmt(elseStmt, st);

        }
    }

    private void checkEmptyStmt(SimpleNode emptyStmt){
        //seems nothing to check
    }

    private void checkWhileStmt(SimpleNode whileStmt){

        SimpleNode condition = whileStmt.jjtGetChild(0);
        checkExpr(condition);

        SimpleNode innerStmt = whileStmt.jjtGetChild(1);

        checkStmt(innerStmt, st);


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
    }
//
//    private static void checkRetStmt(SimpleNode retStmt, SymbolTable st){
//        //how to find function info from return type
//        //assumption? the latest defined function?
//        FunctionType funcType = st.getLatestFuncDef();
//
//        Type retType = funcType.getReturnType();
//
//
//        if(retStmt.jjtGetNumChildren() ==1){
//            //if retStmt has child, then check
//            Type exprType = checkExpr(retStmt.jjtGetChild(0),st);
//            //assertion
//            exprType.assertConvertibleTo(retType);
//        }else if(!(retStmt.jjtGetNumChildren() == 0 && retType.isVoid())){
//            //type void and 0 child of retStmt is also allowed
//            throw new SemanticError("Illegal return type of function",retStmt);
//        }
//
//    }

    private static Type checkExpr(SimpleNode expr, SymbolTable st){
        //expression can be an identifier

        switch (expr.getId()){
            case UcParseTreeConstants.JJTBINARY:
                return checkBinary(expr, st);
            case UcParseTreeConstants.JJTASSIGNMENT:
                return checkAssignment(expr, st);
            case UcParseTreeConstants.JJTUNARYEXPR:
                //the same type as its child, but should we check whether the expression can be applicable to unary op
                return new Type(expr.jjtGetChild(0));
            case UcParseTreeConstants.JJTINTEGERLITERAL:
                return new Type(expr);
            case UcParseTreeConstants.JJTARRAYLOOKUP:
                return checkArrayLookup(expr,st);
            case UcParseTreeConstants.JJTIDENTIFIER:
                return st.findVariable((String)expr.jjtGetValue(),expr);
            case UcParseTreeConstants.JJTFUNCTIONCALL:
                return checkFuncCall(expr,st);

        }
        throw new SemanticError("Illegal Expression", expr);

    }


    private static Type checkBinary(SimpleNode binExpr, SymbolTable st){

        // left associative

        Type lhsType = checkExpr(binExpr.jjtGetChild(0),st);

        //rhs should be literal, identifier , array lookup, !or other expression"
        SimpleNode rhs = binExpr.jjtGetChild(1);
        Type rhsType;
        rhsType = checkExpr(rhs, st);


        //lhsType.assertConvertibleTo(rhsType);

        // how to decide the type after conversion???
        return lhsType.unify(rhsType,rhs);
    }

    private static Type checkAssignment(SimpleNode assExpr, SymbolTable st){

        SimpleNode lhs = assExpr.jjtGetChild(0);

        //check the lhs is legal
        //lhs should only be Identifier or ArrayLookup
        Type lhsType;
        if(lhs.getId() == UcParseTreeConstants.JJTIDENTIFIER){
            lhsType = checkIdentifier(lhs, st);
        }else if(lhs.getId() == UcParseTreeConstants.JJTARRAYLOOKUP){
            lhsType = checkArrayLookup(lhs, st);
        }else{
            throw new SemanticError("Unsupported type in LHS of Assignment", assExpr);
        }

        SimpleNode rhs = assExpr.jjtGetChild(1);
        Type rhsType = checkExpr(rhs, st);

        rhsType.assertConvertibleTo(lhsType);

        // how to decide the type after conversion???
        return lhsType;

    }

    private static Type checkArrayLookup(SimpleNode arrayLookup, SymbolTable st){
        //lookup needs to check whether index is out of bound
        //needs also to check whether type matches
        //eg: int x; x[5];
        // first implementation without checking index
        //array lookup return the primitive type
        Type typeInST = st.findVariable((String) arrayLookup.jjtGetChild(0).jjtGetValue(), arrayLookup);

        if(!typeInST.isArray()){
            throw new SemanticError("Illegal access of a non-array type",arrayLookup);
        }

        //get the index to be accessed from array lookup
        int index = (int)arrayLookup.jjtGetChild(1).jjtGetValue();

        Integer size = typeInST.size();

        if(size!=null){
            //check index < size
            if(!(index < size)){
                throw new SemanticError("Access of array out of index",arrayLookup);
            }
        }

        return typeInST.getArrayType();
    }

    private static Type checkIdentifier(SimpleNode id, SymbolTable st){
        return st.findVariable((String) id.jjtGetValue(), id);
    }

    private static Type checkFuncCall(SimpleNode funcCall, SymbolTable st){
        //check the function has already been defined
        FunctionType funcType = st.findFunction((String)funcCall.jjtGetChild(0).jjtGetValue(),funcCall);

        //number of parameters & type
        Type[] paramList = funcType.getArgumentTypes();

        SimpleNode paramNode = funcCall.jjtGetChild(1);

        if(paramList.length != paramNode.jjtGetNumChildren()){
            throw new SemanticError("parameters don't match",funcCall);
        }

        for(int i=0; i<paramNode.jjtGetNumChildren();i++){
            //check the param in the definition with respect to symbol table

            SimpleNode currentParam = paramNode.jjtGetChild(i);
            Type currentParamType;
            //parameter can be either identifier or literals
            switch(currentParam.getId()){
                case UcParseTreeConstants.JJTIDENTIFIER:
                    currentParamType = checkIdentifier(currentParam,st);
                    break;
                case UcParseTreeConstants.JJTARRAYLOOKUP:
                    currentParamType = checkArrayLookup(currentParam,st);
                case UcParseTreeConstants.JJTINTEGERLITERAL:
                    currentParamType = new Type (Type.Primitive.INT);
                    break;
                default:
                    throw new SemanticError("Unsupported variable type for function call", funcCall);
            }
            currentParamType.assertConvertibleTo(paramList[i]);
        }

        //return the returnType of the function
        return funcType.getReturnType();


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

//    private Type checkAssignment(SimpleNode expr) {
//        Type lhs = checkAssignable(expr.jjtGetChild(0));
//        Type rhs = checkExpr(expr.jjtGetChild(1));
//        rhs.assertConvertibleTo(lhs, expr);
//        return lhs;
//    }

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
