import mips.MIPSGenerator;
import mips.MipsOutputStream;
import mips.MipsWriter;
import parser.*;
import rtl.CodeGenerator;
import semantic.SemanticChecker;
import utils.CommandLine;
import utils.CompileError;

import java.io.*;

/**
 * Created by magnus on 2014-02-07.
 */
public class Main {

    public static void main(String[] arg)
            throws IOException {
        CommandLine cl = CommandLine.parse(arg);
        for (String file : cl.files) {
            try (InputStream is = new FileInputStream(file)) {
                UcParse parser = new UcParse(is);

                SimpleNode tree = parser.Start();
                tree.jjtAccept(new TokenRangeNormaliserVisitor(), null);

                if (cl.printAst) tree.jjtAccept(new ASTPrinterVisitor(), "");

                semantic.Module ast = SemanticChecker.process(tree);
                rtl.Module rtl = CodeGenerator.compileModule(ast);

                if (cl.printRtl) System.out.print(rtl);

                MipsOutputStream os = new MipsWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
                MIPSGenerator generator = new MIPSGenerator(os);
                generator.generateCode(rtl);

            } catch (CompileError error) {
                error.printNicely(file);
                System.err.println();
                System.exit(1);
            }
        }
        System.exit(0);
    }
}
