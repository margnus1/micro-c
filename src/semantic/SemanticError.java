package semantic;
import parser.SimpleNode;

import java.lang.RuntimeException;

/**
 * Created by Magnus on 2014-02-13.
 */
public class SemanticError extends RuntimeException {
    String message;
    SimpleNode node1, node2;
    public SemanticError(String message) {
        super("Semantic error: " + message);
        this.message = message;
    }
    public SemanticError(String message, SimpleNode node) {
        super("Semantic error: " + message);
        this.message = message;
        this.node1 = node;
    }
    public SemanticError(String message, SimpleNode node1, SimpleNode node2) {
        super("Semantic error: " + message);
        this.node1 = node1;
        this.node2 = node2;
    }
}
