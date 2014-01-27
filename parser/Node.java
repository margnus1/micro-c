import java.util.*;

enum Id { PROGRAM, 
        VARDEC, SCALAR_TYPE, ARR_TYPE, FUNC, EXTERN,
        CHAR, INT, VOID,
        COMPOUND_STMNT, STMNT, 
        SIMPLE_COMPOUND_STMNT, EMPTY_STMNT, IF, WHILE, RETURN, 
        EFFECT, ASSIGN, BINARY, UNARY,
        ARRAY, FCALL,
        IDENT, INTEGER_LITERAL}

enum Binop { OROR, ANDAND, EQ, NE,
        LT, GT, LTEQ, GTEQ, 
        PLUS, MINUS,
        MUL, DIV}

enum Unop { NEG, NOT }


public class Node {
    
    private Id id;

    private List<Node> children = new ArrayList<Node>();

    private Position position;

    public Node (Id _id) {
        id = _id;
    }

    public void add(Node child) {
        children.add(child);
    }

    public int numberOfChildren() {
        return children.size();
    }

    public Node getChild(int i) {
        return children.get(i);
    }

    public Node getChild(int i, int n) {
        if (n!=children.size()) 
            throw new IllegalArgumentException ();
        return children.get(i);
    }

    public Id getId() {
        return id;
    }

    public void setPosition(Position p) {
        position = p;
    }

    public Position getPosition () {
        return position;
    }

    public void printHead(String prefix, String suffix) {
        System.out.print(prefix+id);
        if (position != null) {
            System.out.print(" @ "+position);
        }
        System.out.println(" "+suffix);
    }

    public void printChildren(String prefix) {
        if (children != null) {
            for (Node n : children) {
                if (n != null) {
                    n.print(prefix + " ");
                }
            }
        }
    }

    public void print(String prefix, String suffix) {
        printHead(prefix, suffix);
        printChildren(prefix);
    }


    public void print(String prefix) {
        print(prefix, "");
    }
}


class IdentifierNode extends Node {
    private String name;

    public IdentifierNode(String _name, Position p) {
        super(Id.IDENT);
        name = _name;
        setPosition(p);
    }

    public String getName() {
        return name;
    }
    public void print(String prefix) {
        print(prefix, name);
    }
}

class IntegerLiteralNode extends Node {
    private int value;

    public IntegerLiteralNode(int _value, Position p) {
        super(Id.INTEGER_LITERAL);
        value = _value;
        setPosition(p);
    }

    public int getValue() {
        return value;
    }

    public void print(String prefix) {
        print(prefix, value+"");
    }
}

class FuncNode extends Node {
    
    Node ident, returnType, body;

    List<Node> formals;

    public FuncNode(Node _ident, Node _returnType, 
                    List<Node> _formals, Node _body) {
        super(Id.FUNC);
        ident = _ident;
        returnType = _returnType;
        formals = _formals;
        body = _body;
            
    }

    public Node getIdent() {
        return ident;
    }

    public Node getReturnType() {
        return returnType;
    }

    public List<Node> getFormals() {
        return formals;
    }

    public Node getBody() {
        return body;
    }


    public void print(String prefix) {
        String suffix;
        if (ident.getId() == Id.SCALAR_TYPE 
            && ident.getChild(0,1) instanceof IdentifierNode)
            suffix = ((IdentifierNode)ident.getChild(0,1)).getName();
        else 
            suffix = "*** Unexpected function name ***";
        printHead(prefix, suffix);
        returnType.print(prefix+" ");
        System.out.println(prefix+"(");
        for (Node formal : formals) {
            formal.print(prefix+" ");
        }
        System.out.println(prefix+")");
        body.print(prefix+" ");
        printChildren(prefix);
    }
    
}

class ExternNode extends Node {
    
    Node ident, returnType;

    List<Node> formals;

    public ExternNode(Node _ident, Node _returnType, 
                    List<Node> _formals) {
        super(Id.EXTERN);
        ident = _ident;
        returnType = _returnType;
        formals = _formals;
    }

    public Node getIdent() {
        return ident;
    }

    public Node getReturnType() {
        return returnType;
    }

    public List<Node> getFormals() {
        return formals;
    }

    public void print(String prefix) {
        String suffix;
        if (ident.getId() == Id.SCALAR_TYPE 
            && ident.getChild(0,1) instanceof IdentifierNode)
            suffix = ((IdentifierNode)ident.getChild(0,1)).getName();
        else 
            suffix = "*** Unexpected function name ***";
        printHead(prefix, suffix);
        returnType.print(prefix+" ");
        System.out.println(prefix+"(");
        for (Node formal : formals) {
            formal.print(prefix+" ");
        }
        System.out.println(prefix+")");
        printChildren(prefix);
    }

}

class CompoundStatementNode extends Node {
    
    List<Node> declarations, statements;

    public CompoundStatementNode(List<Node> _declarations,
                                 List<Node> _statements) {
        super(Id.COMPOUND_STMNT);
        declarations = _declarations;
        statements = _statements;
    }

    public List<Node> getDeclarations() {
        return declarations;
    }

    public List<Node> getStatements() {
        return statements;
    }

    public void print(String prefix) {
        printHead(prefix, "");
        System.out.println(prefix+"(");
        for (Node d : declarations) {
            d.print(prefix+" ");
        }
        System.out.println(prefix+")");
        System.out.println(prefix+"(");
        for (Node s : statements) {
            s.print(prefix+" ");
        }
        System.out.println(prefix+")");
        printChildren(prefix);
    }
}


class SimpleCompoundStatementNode extends Node {
    
    List<Node> statements;

    public SimpleCompoundStatementNode(List<Node> _statements) {
        super(Id.SIMPLE_COMPOUND_STMNT);
        statements = _statements;
    }

    public List<Node> getStatements() {
        return statements;
    }

    public void print(String prefix) {
        printHead(prefix, "");
        System.out.println(prefix+"(");
        for (Node s : statements) {
            s.print(prefix+" ");
        }
        System.out.println(prefix+")");
        printChildren(prefix);
    }
}

class FCallNode extends Node {
    private Node ident;
    private List<Node> args;
    
    public FCallNode (Node _ident, List<Node> _args) {
        super(Id.FCALL);
        ident = _ident;
        args = _args;
    }

    public Node getIdent() {
        return ident;
    }

    public List<Node> getArgs() {
        return args;
    }
    
    public void print(String prefix) {
        String suffix;
        if (ident instanceof IdentifierNode)
            suffix = ((IdentifierNode)ident).getName();
        else 
            suffix = "*** Unexpected function name in call ***";
        printHead(prefix, suffix);
        System.out.println(prefix+"(");
        for (Node arg : args) {
            arg.print(prefix+" ");
        }
        System.out.println(prefix+")");
        printChildren(prefix);
    }
}

class BinaryNode extends Node {
    private Binop op;
    
    public BinaryNode(Binop _op, Node left, Node right) {
        super(Id.BINARY);
        op = _op;
        add(left);
        add(right);
        setPosition(Position.combine(left.getPosition(), right.getPosition()));
    }

    public Binop getOp() {
        return op;
    }

    public void print(String prefix) {
        print(prefix, op+"");
    }
}


class UnaryNode extends Node {
    private Unop op;
    
    public UnaryNode(Unop _op, Node exp) {
        super(Id.UNARY);
        op = _op;
        add(exp);
    }

    public Unop getOp() {
        return op;
    }

    public void print(String prefix) {
        print(prefix, op+"");
    }
}