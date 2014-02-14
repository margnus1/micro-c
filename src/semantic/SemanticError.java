package semantic;
import parser.SimpleNode;
import parser.Token;

import java.lang.RuntimeException;

/**
 * Created by Magnus on 2014-02-13.
 */
public class SemanticError extends RuntimeException {
    String message;
    SimpleNode node1, node2;
    public SemanticError(String message) {
        this(message, null);
    }
    public SemanticError(String message, SimpleNode node) {
        this(message, node, null);
    }
    public SemanticError(String message, SimpleNode node1, SimpleNode node2) {
        super("Semantic error: " + message);
        this.message = message;
        this.node1 = node1;
        this.node2 = node2;
    }

    /**
     * Pretty-prints this error to std-out
     * @param file The file name to specify in the error message
     */
    public void printNicely(String file) {
        if (node1 != null) printNode(file, node1);
        if (node2 != null) printNode(file, node2);
        System.out.println(file + ": " + message);
    }

    private void printNode(String file, SimpleNode node) {
        System.out.println(file + ": " + node.jjtGetFirstToken().beginLine + ","
        + node.jjtGetFirstToken().beginColumn + ": " + tokenListToString(node.jjtGetFirstToken(), node.jjtGetLastToken()));
    }

    private String tokenListToString(Token first, Token last) {
        StringBuilder b = new StringBuilder();
        while (first != last) {
            b.append(first.image + " ");
            first = first.next;
        }
        b.append(last.image);
        return b.toString();
    }
}
