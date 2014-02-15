package semantic;
import parser.SimpleNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.RuntimeException;

/**
 * Created by Magnus on 2014-02-13.
 */
public class SemanticError extends RuntimeException {
    String message;
    SimpleNode errorNode;
    SimpleNode[] infoNodes;
    public SemanticError(String message, SimpleNode errorNode, SimpleNode... infoNodes) {
        super("Semantic error: " + message);
        if (errorNode == null) throw new IllegalArgumentException("The node cannot be null");
        this.message = message;
        this.errorNode = errorNode;
        this.infoNodes = infoNodes;
    }

    private static boolean hasAnsi = !System.getProperty("os.name").startsWith("Windows");
    public static final String ansiWhite = hasAnsi ? "\u001B[1m\u001B[37m" : "";
    public static final String ansiRed   = hasAnsi ? "\u001B[1m\u001b[31m" : "";
    public static final String ansiGreen = hasAnsi ? "\u001B[1m\u001b[32m" : "";
    public static final String ansiReset = hasAnsi ? "\u001b[0m"           : "";

    /**
     * Pretty-prints this error to std-err
     * @param file The file name to specify in the error message
     */
    public void printNicely(String file) {
        System.err.println(ansiWhite + (errorNode == null ? "" : formatPosition(file, errorNode))
                    + ansiRed + " error: " + ansiWhite + message + ansiReset);
        if (errorNode != null)
            printHighlighted(errorNode, readLine(file, errorNode.jjtGetFirstToken().beginLine - 1));
        for (SimpleNode node : infoNodes)
            if (node != null) {
                System.out.println(ansiWhite + formatPosition(file, node) + ansiReset);
                printHighlighted(node, readLine(file, node.jjtGetFirstToken().beginLine - 1));
            }
    }

    private String formatPosition(String file, SimpleNode node) {
        String[] parts = file.split("[/\\\\]");
        String name = parts[parts.length-1];
        return name + ":" + node.jjtGetFirstToken().beginLine + ":"
                + node.jjtGetFirstToken().beginColumn + ":";
    }

    private void printHighlighted(SimpleNode node, String line) {
        System.err.println(line);
        StringBuilder underline = new StringBuilder();
        int beginColumn = node.jjtGetFirstToken().beginColumn - 1;
        for (int count = 0; count < beginColumn; count++)
            underline.append(' ');
        int endColumn;
        if (node.jjtGetFirstToken().beginLine < node.jjtGetLastToken().endLine)
            endColumn = line.replace("\t", "        ").length();
        else endColumn = node.jjtGetLastToken().endColumn;
        underline.append('^');
        for (int count = beginColumn + 1; count < endColumn; count++)
             underline.append('~');
        if (node.jjtGetFirstToken().beginLine < node.jjtGetLastToken().endLine)
            underline.append("...");
        System.err.println(ansiGreen + underline + ansiReset);
    }

    private String readLine(String file, int number) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (number-- > 0) br.readLine();
            return br.readLine();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return "";
        }
    }
}
