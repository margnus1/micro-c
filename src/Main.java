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
        if (arg.length != 1) {
            System.out.println("Usage: UcParse <input file name>");
            System.exit(0);
        }
        InputStream is = new FileInputStream(arg[0]);
        UcParse parser = new UcParse(is);

        try {
            SimpleNode tree = parser.Start();
            tree.jjtAccept(new TokenRangeNormaliserVisitor(), null);

            SemanticChecker.start(tree);
            tree.jjtAccept(new UcParseVisitor<String>() {
                @Override
                public void visit(SimpleNode node, String data) {
                    System.out.print(data + node);
                    if (node.jjtGetValue() != null)
                        System.out.print(" " + node.jjtGetValue());
                    System.out.println();
                    node.childrenAccept(this, data + "  ");
                }
            }, "");
            System.exit(0);
        } catch (TokenMgrError lexicalError) {
            System.err.println(lexicalError.getMessage());
        } catch (ParseException parseError) {
            System.err.println("Parse error: " + parseError.getMessage());
        } catch (SemanticError semanticError) {
            semanticError.printNicely(arg[0]);
        }
        System.exit(1);
    }
}
