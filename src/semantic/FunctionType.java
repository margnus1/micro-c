package semantic;
import  parser.*;

/**
 * Created by Doris on 14-2-13.
 */
public class FunctionType {
    private Type returnType;
    private Type[] argumentTypes;
    private SimpleNode node;

    /**
     * Creates a FunctionType from a FunctionDeclaration or FunctionDefinition AST node.
     * @param node The AST node.
     */
    public FunctionType(SimpleNode node){
        this.node = node;
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
    public SimpleNode getNode() {
        return node;
    }

    @Override
    public String toString() {
        StringBuilder tsBuilder = new StringBuilder();
        tsBuilder.append(returnType);
        if (node != null) tsBuilder.append(" " + node.jjtGetChild(1).jjtGetValue());
        tsBuilder.append('(');
        for (Type a : argumentTypes)
            tsBuilder.append(a + ", ");
        if (argumentTypes.length != 0)
            tsBuilder.delete(tsBuilder.length() - 2, tsBuilder.length());
        tsBuilder.append(')');
        return tsBuilder.toString();
    }
}
