package semantic;
import parser.SimpleNode;
import utils.CompileError;
import utils.Position;

/**
 * Created by Magnus on 2014-02-13.
 */
public class SemanticError extends CompileError {
    public SemanticError(String message, SimpleNode errorNode, SimpleNode... infoNodes) {
        super(message, new Position(errorNode), null, positions(infoNodes));
        if (errorNode == null) throw new IllegalArgumentException("The node cannot be null");
    }

    private static Position[] positions(SimpleNode[] infoNodes) {
        Position[] positions = new Position[infoNodes.length];
        for (int i = 0; i < positions.length; i++)
            positions[i] = new Position(infoNodes[i]);
        return positions;
    }
}
