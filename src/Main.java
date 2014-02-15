import parser.*;
import semantic.SemanticChecker;
import semantic.SemanticError;

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
            if (arg.length > 1)
                System.out.println(file);
            InputStream is = new FileInputStream(file);
            UcParse parser = new UcParse(is);

            try {
                SimpleNode tree = parser.Start();
                tree.jjtAccept(new TokenRangeNormaliserVisitor(), null);

                // tree.jjtAccept(new ASTPrinterVisitor(), "");
                SemanticChecker semantic = new SemanticChecker();
                semantic.start(tree);
                continue;
            } catch (TokenMgrError lexicalError) {
                System.err.println(lexicalError.getMessage());
            } catch (ParseException parseError) {
                System.err.println("Parse error: " + parseError.getMessage());
            } catch (SemanticError semanticError) {
                semanticError.printNicely(file);
            }
            //System.exit(1);
        }
        System.exit(0);
    }
}
