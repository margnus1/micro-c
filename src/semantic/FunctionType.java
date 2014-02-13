package semantic;
import  parser.*;

/**
 * Created by Doris on 14-2-13.
 */
public class FunctionType {
    private Type returnType;
    private Type[] argumentTypes;

    /**
     * Creates a FunctionType from a FunctionDeclaration or FunctionDefinition AST node.
     * @param node The AST node.
     */
    public FunctionType(SimpleNode node){
        assert(node.getId() == UcParseTreeConstants.JJTFUNCTIONDECLARATION ||
               node.getId() == UcParseTreeConstants.JJTFUNCTIONDEFINITION);
        returnType = new Type(node.jjtGetChild(0));
        SimpleNode args = node.jjtGetChild(2);
        argumentTypes = new Type[args.jjtGetNumChildren()];
        for (int i = 0; i < argumentTypes.length; i++) {
            argumentTypes[i] = new Type(args.jjtGetChild(i));
        }
    }

    public Type getReturnType() {
        return returnType;
    }

    public Type[] getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public String toString() {
        return toString(null);
    }

    /**
     * Generates a function specification including the name of the function
     * @param name Name of the function
     */
    public String toString(String name) {
        StringBuilder tsBuilder = new StringBuilder();
        tsBuilder.append(returnType);
        if (name != null) tsBuilder.append(" " + name);
        tsBuilder.append('(');
        for (Type a : argumentTypes)
            tsBuilder.append(a + ", ");
        tsBuilder.delete(tsBuilder.length() - 3, tsBuilder.length() - 1);
        tsBuilder.append(')');
        return tsBuilder.toString();
    }
}
