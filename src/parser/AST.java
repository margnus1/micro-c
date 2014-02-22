package parser;

/**
 * Created by Magnus on 2014-02-21.
 */
public class AST {
    private AST(){}

    public static String getVarName(SimpleNode declaration) {
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
}
