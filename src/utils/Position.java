package utils;

import parser.SimpleNode;
import parser.Token;

public class Position {
    int beginLine, beginColumn, endLine, endColumn;

    public Position(int beginLine, int beginColumn, int endLine, int endColumn) {
        this.beginLine   = beginLine;
        this.beginColumn = beginColumn;
        this.endLine     = endLine;
        this.endColumn   = endColumn;
    }

    public Position(Token begin, Token end){
        this.beginLine   = begin.beginLine;
        this.beginColumn = begin.beginColumn;
        this.endLine     = end.endLine;
        this.endColumn   = end.endColumn;
    }

    public Position(SimpleNode node) {
        this.beginLine   = node.jjtGetFirstToken().beginLine;
        this.beginColumn = node.jjtGetFirstToken().beginColumn;
        this.endLine     = node.jjtGetLastToken().endLine;
        this.endColumn   = node.jjtGetLastToken().endColumn;
    }

    public String toString() {
        return beginLine + ":" + beginColumn;
    }
}
