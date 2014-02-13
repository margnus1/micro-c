package semantic;
import parser.SimpleNode;
import parser.UcParse;
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
     * Constructs the type of a literal, variable declaration, or formal declaration.
     * @param node The node containing a literal, variable declaration or formal declaration
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
        }
    }

    private Primitive primitiveFromBaseType(parser.Type type) {
        switch (type) {
            case INT:  return Primitive.INT;
            case CHAR: return Primitive.CHAR;
            case VOID: return Primitive.VOID;
            default: throw new RuntimeException("Unexpected base type in AST");
        }
    }

    private Type(Primitive t) {
        type = t;
    }

    /**
     * Asserts that this type can be converted to @param t
     * @param t The type that is conveted to
     */
    public void canBeConvertedTo(Type t) {
        //return TRUE;
    }

    /**
     * Constructs the result type of a binary operation of two types
     * @param other The other type
     * @param expressionThatUnifies The entire expression.
     * @return Unified type
     */
    public Type unify(Type other, SimpleNode expressionThatUnifies) {
        //if (bad) throw new SemanticError();
        //else return INT;
    }
}
