import parser.*;
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
            tree.jjtAccept(new UcParseVisitor() {
                @Override
                public Object visit(SimpleNode node, Object data) {
                    System.out.println((String)data + node + " " + node.jjtGetValue());
                    node.childrenAccept(this, (String)data + "  ");
                    return null;
                }
            }, "");
        } catch (TokenMgrError lexicalError) {
            System.err.println(lexicalError.getMessage());
        } catch (ParseException parseError) {
            System.err.println("Parse error: " + parseError.getMessage());
        }
    }
}
