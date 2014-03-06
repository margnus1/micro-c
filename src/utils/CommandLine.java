package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magnus on 3/6/14.
 */
public class CommandLine {
    public boolean printAst = false;
    public boolean printRtl = false;
    public boolean mangleSymbols = false;
    public boolean stdout = false;
    public List<String> files = new ArrayList<>();

    public static CommandLine parse(String[] argv) {
        CommandLine cl = new CommandLine();

        for (int i = 0; i < argv.length; i++) {
            if(argv[i].startsWith("--")) {
                switch (argv[i].substring(2)) {
                    case "print-ast":      cl.printAst      = true; break;
                    case "print-rtl":      cl.printRtl      = true; break;
                    case "mangle-symbols": cl.mangleSymbols = true; break;
                    case "stdout":         cl.stdout        = true; break;
                    default:
                        System.err.println("Unknown option " + argv[i]);
                }
            } else {
                cl.files.add(argv[i]);
            }
        }

        if (cl.files.size() == 0) {
            System.out.println("Usage: java Main [" +
                    ANSI.highlightSubstitutionSymbol("arguments") +
                    "] " + ANSI.highlightSubstitutionSymbol("input file names"));
            System.out.println("Arguments:");
            System.out.println("    --print-ast         Pretty-print the AST.");
            System.out.println("    --print-rtl         Pretty-print the generated RTL intermediate.");
            System.out.println("    --mangle-symbols    Mangle symbol names.");
            System.out.println("    --stdout            Print resulting assembly to stdout rather than a file.");
            System.exit(0);
        }

        return cl;
    }
}
