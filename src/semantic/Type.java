package semantic;
import parser.SimpleNode;
import parser.UcParseTreeConstants;

import java.lang.*;

/**
 * Created by Magnus on 2014-02-13.
 */
public class Type {
    private enum Primitive { VOID, INT, CHAR, LITERAL, ARRAY }
    private Primitive type;
    // Non-null iff type==ARRAY
    private Type of;
    // Null if type!=ARRAY
    private Integer size;
    // The expression generating the type. Is allowed to be null.
    private SimpleNode expr;

    /**
     * Constructs the type of a literal, variable declaration, formal declaration or base type.
     * @param node The node containing a literal, variable declaration, formal declaration, or baes type.
     */
    public Type(SimpleNode node) {
        expr = node;
        switch (node.getId()) {
            case UcParseTreeConstants.JJTVARIABLEDECLARATION:
                Primitive primitive = primitiveFromBaseType((parser.Type)node.jjtGetChild(0).jjtGetValue());
                SimpleNode declarator = node.jjtGetChild(1);
                if (declarator.getId() == UcParseTreeConstants.JJTARRAYDECLARATOR) {
                    type = Primitive.ARRAY;
                    of = new Type(primitive);
                    if (declarator.jjtGetNumChildren() == 2)
                        size = (Integer)declarator.jjtGetChild(1).jjtGetValue();
                } else {
                    type = primitive;
                }
                break;
            case UcParseTreeConstants.JJTBASETYPE:
                type = primitiveFromBaseType((parser.Type)node.jjtGetValue());
                break;
            case UcParseTreeConstants.JJTINTEGERLITERAL:
                type = Primitive.LITERAL;
                break;
            default:
                throw new RuntimeException("Unexpected AST node type \"" + node + "\" in Type constructor");
        }
    }

    private Primitive primitiveFromBaseType(parser.Type type) {
        switch (type) {
            case INT:  return Primitive.INT;
            case CHAR: return Primitive.CHAR;
            case VOID: return Primitive.VOID;
            default: throw new RuntimeException("Unexpected base type " + type + " in AST");
        }
    }

    private Type(Primitive t) {
        type = t;
    }

    private Type(Type base, SimpleNode expr) {
        type = base.type;
        of = base.of;
        size = base.size;
        this.expr = expr;
    }

    /**
     * Asserts that this type can be converted to @param t
     * @param t The type that is converted to
     */
    public void assertConvertibleTo(Type t) {
        if (!(this.canBeConvertedTo(t)))
            throw new SemanticError(this + " can't be converted to a " + t, this.expr, t.expr);
    }

    private boolean canBeConvertedTo(Type t) {
        if (type == Primitive.ARRAY) {
            return t.type    == Primitive.ARRAY
                && t.of.type == of.type;
        }
        if (type == t.type) return true;
        if (type == Primitive.LITERAL &&
            isIntegral(t.type)) return true;
        return false;
    }

    private boolean isIntegral(Primitive p) {
        switch (p) {
            case INT: case CHAR: case LITERAL: return true;
            default: return false;
        }
    }

    /**
     * Constructs the result type of a binary operation of two types
     * @param other The other type
     * @param expressionThatUnifies The entire expression.
     * @return Unified type
     */
    public Type unify(Type other, SimpleNode expressionThatUnifies) {
        if (this.canBeConvertedTo(other)) return new Type(other, expressionThatUnifies);
        if (other.canBeConvertedTo(this)) return new Type(this,  expressionThatUnifies);
        throw new SemanticError("Types " + this + " and " + other + " are not unifiable", this.expr, other.expr);
    }

    public SimpleNode getExpr() {
        return expr;
    }

    public boolean hasSize() {
        return size != null;
    }

    @Override
    public boolean equals(Object b) {
        if (!(b instanceof Type)) return false;
        Type t = (Type)b;
        if (type != t.type) return false;
        return (type != Primitive.ARRAY || of.equals(t.of));
    }

    @Override
    public String toString() {
        switch (type) {
            case VOID:    return "void";
            case LITERAL: return "*int or char*";
            case INT:     return "int";
            case CHAR:    return "char";
            case ARRAY:   return of.toString() + "[" + (size == null ? "" : size) + "]";
            default: throw new RuntimeException("Unexpected type");
        }
    }
}
