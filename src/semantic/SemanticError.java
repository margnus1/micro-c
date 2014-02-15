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

    private static boolean hasAnsi = !System.getProperty("os.name").startsWith("Windows");
    private static final String ansiWhite = hasAnsi ? "\u001B[1m\u001B[37m" : "";
    private static final String ansiRed   = hasAnsi ? "\u001B[1m\u001b[31m" : "";
    private static final String ansiGreen = hasAnsi ? "\u001B[1m\u001b[32m" : "";
    private static final String ansiReset = hasAnsi ? "\u001b[0m"           : "";

    /**
     * Pretty-prints this error to std-out
     * @param file The file name to specify in the error message
     */
    public void printNicely(String file) {
        System.out.println(ansiWhite + formatPosition(file, node1)
                    + ansiRed + " error: " + ansiWhite + message + ansiReset);
        printHighlighted(node1, readLine(file, node1.jjtGetFirstToken().beginLine - 1));
        if (node2 != null) {
            System.out.println(ansiWhite + formatPosition(file, node2) + ansiReset);
            printHighlighted(node2, readLine(file, node2.jjtGetFirstToken().beginLine - 1));
        }
    }

    private String formatPosition(String file, SimpleNode node) {
        String[] parts = file.split("[/\\\\]");
        String name = parts[parts.length-1];
        return name + ":" + node.jjtGetFirstToken().beginLine + ":"
                + node.jjtGetFirstToken().beginColumn + ":";
    }

    private void printHighlighted(SimpleNode node, String line) {
        System.out.println(line);
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
        if (node.jjtGetFirstToken().beginLine < node.jjtGetLastToken().endLine) underline.append("...");
        System.out.println(ansiGreen + underline + ansiReset);
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
