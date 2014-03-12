package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton command line parser
 */
public final class CommandLine {
    private CommandLine() {}
    public static boolean printAst = false;
    public static boolean printRtl = false;
    public static boolean mangleSymbols = false;
    public static boolean stdout = false;
    public static boolean commentAssembly = false;
    public static List<String> files = new ArrayList<>();

    public static void parse(String[] argv) {
        for (int i = 0; i < argv.length; i++) {
            if(argv[i].startsWith("--")) {
                switch (argv[i].substring(2)) {
                    case "print-ast":        printAst        = true; break;
                    case "print-rtl":        printRtl        = true; break;
                    case "mangle-symbols":   mangleSymbols   = true; break;
                    case "stdout":           stdout          = true; break;
                    case "comment-assembly": commentAssembly = true; break;
                    default:
                        System.err.println("Unknown option " + argv[i]);
                }
            } else {
                files.add(argv[i]);
            }
        }

        if (files.size() == 0) {
            System.out.println("Usage: java Main [" +
                    ANSI.highlightSubstitutionSymbol("arguments") +
                    "] " + ANSI.highlightSubstitutionSymbol("input file names"));
            System.out.println("Arguments:");
            System.out.println("    --print-ast         Pretty-print the AST.");
            System.out.println("    --print-rtl         Pretty-print the generated RTL intermediate.");
            System.out.println("    --mangle-symbols    Mangle symbol names.");
            System.out.println("    --stdout            Print resulting assembly to stdout rather than a file.");
            System.out.println("    --comment-assembly  Add comments to output assembly.");
        }
    }
}
