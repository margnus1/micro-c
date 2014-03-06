package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magnus on 3/6/14.
 */
public class CommandLine {
    public boolean printAst = false;
    public boolean printRtl = false;
    public List<String> files = new ArrayList<>();

    public static CommandLine parse(String[] argv) {
        CommandLine cl = new CommandLine();

        for (int i = 0; i < argv.length; i++) {
            if(argv[i].startsWith("--")) {
                switch (argv[i].substring(2)) {
                    case "print-ast": cl.printAst = true; break;
                    case "print-rtl": cl.printRtl = true; break;
                    default:
                        System.err.println("Unknown option " + argv[i]);
                }
            } else {
                cl.files.add(argv[i]);
            }
        }

        if (cl.files.size() == 0) {
            System.out.println("Usage: java Main [--print-ast] [--print-rtl] <input file name>");
            System.exit(0);
        }

        return cl;
    }
}
