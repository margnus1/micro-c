package parser;

/**
 * JJTree does not always generate correct token extents, in particular for in-grammar node generation directives.
 * This visitor widens the token extents of all nodes to envelop all it's children, helping the problem a bit.
 */
public class TokenRangeNormaliserVisitor implements UcParseVisitor<TokenRangeNormaliserVisitor.Extents> {
    @Override
    public void visit(SimpleNode node, Extents data) {
        if (data != null) {
            data.extend(node.jjtGetFirstToken());
            data.extend(node.jjtGetLastToken());
        }
        Extents myExtents = new Extents(node);
        node.childrenAccept(this, myExtents);
        node.jjtSetFirstToken(myExtents.first);
        node.jjtSetLastToken(myExtents.last);
    }

    static class Extents {
        public Token first;
        public Token last;

        public Extents(SimpleNode node) {
            first = node.jjtGetFirstToken();
            last  = node.jjtGetLastToken();
        }

        public void extend(Token t) {
            if (tokenPreceeds(t, first)) first = t;
            if (tokenPreceeds(last, t))  last  = t;
        }
        private boolean tokenPreceeds(Token t1, Token t2) {
            return t1.beginLine < t2.beginLine ||
                    (t1.beginLine == t2.beginLine && t1.beginColumn <= t2.beginColumn);
        }
    }
}
