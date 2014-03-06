package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Magnus on 2014-02-17.
 */
public abstract class CompileError extends RuntimeException {
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
        System.err.println(ANSI.bold + formatPosition(file, position)
                + ANSI.red + " error: " + ANSI.defaultForeground + message + ANSI.reset);
    }

    private static String formatPosition(String file, Position pos) {
        String name = Path.fileName(file);
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
                System.out.println(ANSI.bold + formatPosition(file, pos) + ANSI.reset);
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
        System.err.println(ANSI.bold + ANSI.green + underline + ANSI.reset);
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
