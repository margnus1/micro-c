import parser.*;
import rtl.CodeGenerator;
import semantic.SemanticChecker;
import utils.CompileError;

import java.io.*;

/**
 * Created by magnus on 2014-02-07.
 */
public class Main {

    public static void main(String[] arg)
            throws IOException {
        if (arg.length == 0) {
            System.out.println("Usage: UcParse <input file name>");
            System.exit(0);
        }
        for (String file : arg) {
            try (InputStream is = new FileInputStream(file)) {
                UcParse parser = new UcParse(is);

                SimpleNode tree = parser.Start();
                tree.jjtAccept(new TokenRangeNormaliserVisitor(), null);

                 tree.jjtAccept(new ASTPrinterVisitor(), "");
                semantic.Module ast = SemanticChecker.process(tree);
                rtl.Module rtl = CodeGenerator.compileModule(ast);
                System.out.print(rtl);
            } catch (CompileError error) {
                error.printNicely(file);
                System.err.println();
                //System.exit(1);
            }
        }
        System.exit(0);
    }
}
