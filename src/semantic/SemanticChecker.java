package semantic;

import parser.*;

/**
 * Created by Doris on 14-2-13.
 */
public class SemanticChecker {
    private SemanticChecker() {}
    SymbolTable st = new SymbolTable();
    FunctionType currentFunctionContext = null;

    public static Module process(SimpleNode root) {
        SemanticChecker sc = new SemanticChecker();
        sc.start(root);
        return sc.st.toModule();
    }

    public void start(SimpleNode root){
        for(SimpleNode child : root){
            switch (child.getId()){
                case UcParseTreeConstants.JJTFUNCTIONDEFINITION:
                    st.addFunctionDeclaration(child);
                    checkFuncDef(child);
                    break;
                case UcParseTreeConstants.JJTFUNCTIONDECLARATION:
                    st.addFunctionDeclaration(child);
                    break;
                case UcParseTreeConstants.JJTVARIABLEDECLARATION:
                    st.addVariable(child);
                    break;
                default:
                    throw new RuntimeException("Unexpected node " + child + " in AST.");
            }
        }
    }

    public void checkFuncDef(SimpleNode funcDef){
        //insert into symbol table
        currentFunctionContext = st.addFunctionDefinition(funcDef);

        //create a new Scope
        st.enterScope(funcDef.jjtGetChild(3));

        //check FunctionParameters, the third child
        //All children are VariableDeclaration-s
        for(SimpleNode varDec : funcDef.jjtGetChild(2))
            st.addVariable(varDec);

        //check CompoundStatement, the 4th child
        checkFuncCompStmt(funcDef.jjtGetChild(3));

        //exit the scope
        st.exitScope();
        currentFunctionContext = null;
    }

    /**
     * Check a CompoundStatement, assuming that a new scope already has been created
     */
    public void checkFuncCompStmt(SimpleNode compStmt){
        //For CompoundStatements within a FunctionDefinition,
        //the new Scope is created by checkFuncDef,
        //while the standalone CompoundStatements has their
        //scopes created by checkCompStmt

        //Add each variable in the first child, VariableDeclarations
        for(SimpleNode varDec : compStmt.jjtGetChild(0))
            st.addVariable(varDec);

        //second child, Statements
        for(SimpleNode stmt : compStmt.jjtGetChild(1))
            checkStmt(stmt);
    }

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
        checkExpr(condition).assertArithmetic(ifStmt);

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
        checkExpr(condition).assertArithmetic(whileStmt);

        SimpleNode innerStmt = whileStmt.jjtGetChild(1);
        checkStmt(innerStmt);
    }

    private void checkSimpleCompStmt(SimpleNode simpleCompStmt){
        //create a new scope
        st.enterScope(simpleCompStmt);

        for(SimpleNode stmt : simpleCompStmt)
            checkStmt(stmt);

        //exit the scope
        st.exitScope();
    }

    private void checkRetStmt(SimpleNode retStmt){
        assert currentFunctionContext != null;
        if (currentFunctionContext.getReturnType().isVoid()) {
            if (retStmt.jjtGetNumChildren() != 0)
                throw new SemanticError("Value returned from void function.",
                        retStmt, currentFunctionContext.getReturnType().getExpr());
        } else {
            if (retStmt.jjtGetNumChildren() != 1)
                throw new SemanticError("Return from non-void function is missing a value.",
                        retStmt, currentFunctionContext.getReturnType().getExpr());
            checkExpr(retStmt.jjtGetChild(0))
                    .assertConvertibleTo(currentFunctionContext.getReturnType(), retStmt);
        }
    }

    private Type checkExpr(SimpleNode expr){
        switch (expr.getId()){
            case UcParseTreeConstants.JJTASSIGNMENT:
                return checkAssignment(expr);
            case UcParseTreeConstants.JJTARRAYLOOKUP:
                return checkArrayLookup(expr);
            case UcParseTreeConstants.JJTIDENTIFIER:
                return st.findVariable(expr);
            case UcParseTreeConstants.JJTBINARY:
                return checkBinary(expr);
            case UcParseTreeConstants.JJTUNARYEXPR:
                Type t = checkExpr(expr.jjtGetChild(0));
                t.assertArithmetic(expr);
                return t;
            case UcParseTreeConstants.JJTFUNCTIONCALL:
                return checkFuncCall(expr);
            case UcParseTreeConstants.JJTINTEGERLITERAL:
                return new Type(expr);
            case UcParseTreeConstants.JJTSTRINGLITERAL:
                return new Type(expr);
            default:
                throw new RuntimeException("Unexpected " + expr + " in AST. Expected expression.");
        }
    }

    private Type checkBinary(SimpleNode binExpr){
        Type lhsType = checkExpr(binExpr.jjtGetChild(0));
        lhsType.assertArithmetic(binExpr);

        Type rhsType = checkExpr(binExpr.jjtGetChild(1));
        rhsType.assertArithmetic(binExpr);

        return lhsType.unify(rhsType, binExpr);
    }

    private Type checkAssignment(SimpleNode assExpr){
        SimpleNode lhs = assExpr.jjtGetChild(0);

        //check the lhs is legal
        //lhs should only be Identifier or ArrayLookup
        Type lhsType;
        if(lhs.getId() == UcParseTreeConstants.JJTIDENTIFIER){
            lhsType = st.findVariable(lhs);
            if (lhsType.isVector())
                throw new SemanticError("You cannot assign to a vector.", assExpr, lhsType.getExpr());
        }else if(lhs.getId() == UcParseTreeConstants.JJTARRAYLOOKUP){
            lhsType = checkArrayLookup(lhs);
        }else{
            throw new SemanticError("Left hand side of an assignment must be a variable.", lhs);
        }

        Type rhsType = checkExpr(assExpr.jjtGetChild(1));
        rhsType.assertConvertibleTo(lhsType, assExpr);

        return lhsType;
    }

    private Type checkArrayLookup(SimpleNode arrayLookup){
        // lookup never checks whether a lookup is within bounds in C,
        // some compilers issue warnings when the index is a literal our of bounds.
        Type arrT = st.findVariable(arrayLookup.jjtGetChild(0));
        if(!arrT.isIndexable())
            throw new SemanticError(arrT + " cannot be indexed.", arrayLookup, arrT.getExpr());

        Type indexT = checkExpr(arrayLookup.jjtGetChild(1));
        indexT.assertArithmetic(arrayLookup);

        arrayLookup.jjtSetValue(arrT.getElementType().getRtlType());

        return arrT.getElementType();
    }

    private Type checkFuncCall(SimpleNode expr) {
        SimpleNode func = expr.jjtGetChild(0);
        if (func.getId() != UcParseTreeConstants.JJTIDENTIFIER)
            throw new SemanticError("A function must be an identifier.", func);
        FunctionType ft = st.findFunction(func);

        SimpleNode args = expr.jjtGetChild(1);
        if (args.jjtGetNumChildren() != ft.getArgumentTypes().length)
            throw new SemanticError("Wrong number of arguments in call to function " + func.jjtGetValue() + ".",
                    expr, ft.getNode());
        for (int i = 0; i < args.jjtGetNumChildren(); i++)
            checkExpr(args.jjtGetChild(i)).assertConvertibleTo(ft.getArgumentTypes()[i], args.jjtGetChild(i));

        return ft.getReturnType();
    }
}
