package parser;

/**
* Created by magnus on 2014-02-15.
*/
public class ASTPrinterVisitor implements UcParseVisitor<String> {
    @Override
    public void visit(SimpleNode node, String data) {
        System.out.print(data + node);
        if (node.jjtGetValue() != null)
            System.out.print(" " + node.jjtGetValue());
        System.out.println();
        node.childrenAccept(this, data + "  ");
    }
}
