import mips.MIPSGenerator;
import mips.MipsOutputStream;
import mips.MipsWriter;
import parser.*;
import rtl.CodeGenerator;
import rtl.Mangler;
import semantic.SemanticChecker;
import utils.CommandLine;
import utils.CompileError;
import utils.Path;

import java.io.*;

/**
 * Created by magnus on 2014-02-07.
 */
public class Main {

    public static void main(String[] arg)
            throws IOException {
        CommandLine.parse(arg);
        for (String file : CommandLine.files) {
            try (InputStream is = new FileInputStream(file)) {
                UcParse parser = new UcParse(is);

                SimpleNode tree = parser.Start();
                tree.jjtAccept(new TokenRangeNormaliserVisitor(), null);
                if (CommandLine.printAst) tree.jjtAccept(new ASTPrinterVisitor(), "");

                semantic.Module ast = SemanticChecker.process(tree);
                rtl.Module rtl = CodeGenerator.compileModule(ast);
                if (CommandLine.mangleSymbols) rtl = Mangler.mangle(rtl);
                if (CommandLine.printRtl) System.out.print(rtl);

                if (CommandLine.stdout) {
                    MipsOutputStream os = new MipsWriter(new OutputStreamWriter(System.out));
                    MIPSGenerator.generateCode(os, rtl);
                } else {
                    String outputFile = Path.baseName(Path.fileName(file)) + ".s";
                    try (FileWriter fw = new FileWriter(outputFile)) {
                        MipsOutputStream os = new MipsWriter(new BufferedWriter(fw));
                        MIPSGenerator.generateCode(os, rtl);
                    }
                }

            } catch (CompileError error) {
                error.printNicely(file);
                System.err.println();
                System.exit(1);
            }
        }
        System.exit(0);
    }
}
