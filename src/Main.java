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

            SemanticChecker.start(tree);

            tree.jjtAccept(new UcParseVisitor() {
                @Override
                public Object visit(SimpleNode node, Object data) {
                    System.out.print((String)data + node);
                    if (node.jjtGetValue() != null)
                        System.out.print(" " + node.jjtGetValue());
                    System.out.println();
                    node.childrenAccept(this, (String)data + "  ");
                    return null;
                }
            }, "");
        } catch (TokenMgrError lexicalError) {
            System.err.println(lexicalError.getMessage());
        } catch (ParseException parseError) {
            System.err.println("Parse error: " + parseError.getMessage());
        } catch (SemanticError semanticError) {
            semanticError.printNicely(arg[0]);
        }
    }
}
