package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Magnus on 2014-02-17.
 */
public abstract class CompileError extends RuntimeException {
    private static boolean hasAnsi = !System.getProperty("os.name").startsWith("Windows");
    public static final String ansiBold              = hasAnsi ? "\u001B[1m"  : "";
    public static final String ansiRed               = hasAnsi ? "\u001b[31m" : "";
    public static final String ansiGreen             = hasAnsi ? "\u001b[32m" : "";
    public static final String ansiDefaultForeground = hasAnsi ? "\u001b[39m" : "";
    public static final String ansiReset             = hasAnsi ? "\u001b[0m"  : "";

    private Position error;
    private String infoMessage;
    private Position[] infos;

    public CompileError(String message, Position error, String infoMessage, Position... infos) {
        super(message);
        this.error = error;
        this.infoMessage = infoMessage;
        this.infos = infos;
    }

    public static void printErrorHeader(String file, Position position, String message) {
        System.err.println(ansiBold + formatPosition(file, position)
                + ansiRed + " error: " + ansiDefaultForeground + message + ansiReset);
    }

    private static String formatPosition(String file, Position pos) {
        String[] parts = file.split("[/\\\\]");
        String name = parts[parts.length-1];
        return name + ":" + (pos == null ? "" : pos + ":");
    }

    /**
     * Pretty-prints this error to std-err
     * @param file The file name to specify in the error message
     */
    public void printNicely(String file) {
        printErrorHeader(file, error, getMessage());
        if (error != null)
            printHighlighted(error, readLine(file, error.beginLine - 1));
        if (infoMessage != null)
            System.err.println(infoMessage);
        for (Position pos : infos)
            if (pos != null) {
                System.out.println(ansiBold + formatPosition(file, pos) + ansiReset);
                printHighlighted(pos, readLine(file, pos.beginLine - 1));
            }
    }

    private static void printHighlighted(Position pos, String line) {
        System.err.println(line);
        StringBuilder underline = new StringBuilder();
        int beginColumn = pos.beginColumn - 1;
        for (int count = 0; count < beginColumn; count++)
            underline.append(' ');
        int endColumn;
        if (pos.beginLine < pos.endLine)
            endColumn = line.replace("\t", "        ").length();
        else endColumn = pos.endColumn;
        underline.append('^');
        for (int count = beginColumn + 1; count < endColumn; count++)
             underline.append('~');
        if (pos.beginLine < pos.endLine)
            underline.append("...");
        System.err.println(ansiBold + ansiGreen + underline + ansiReset);
    }

    private static String readLine(String file, int number) {
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
