package semantic;
import  parser.*;

/**
 * Created by Doris on 14-2-13.
 */
public class FunctionType {
    private Type returnType;
    private Type[] argumentTypes;
    private SimpleNode node;
    private SimpleNode definition;

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
            if (argumentTypes[i].hasSize())
                throw new SemanticError("An array formal may not have a size.", args.jjtGetChild(i));
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
    public SimpleNode getDefinition() {
        return definition;
    }
    public void setDefinition(SimpleNode definition) {
        this.definition = definition;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FunctionType)) return false;
        FunctionType f2 = (FunctionType)other;
        if (this.argumentTypes.length != f2.argumentTypes.length) return false;
        if (!this.returnType.equals(f2.returnType)) return false;
        for (int i = 0; i < argumentTypes.length; i++)
            if (!this.argumentTypes[i].equals(f2.argumentTypes[i])) return false;
        return true;
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
