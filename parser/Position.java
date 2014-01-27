public class Position {
    private int beginLine, beginColumn, endLine, endColumn;
    
    private Position() {}

    public static Position fromToken(Token t){
        Position p = new Position();
        p.beginLine = t.beginLine;
        p.beginColumn = t.beginColumn;
        p.endLine = t.endLine;
        p.endColumn = t.endColumn;
        return p;
    }

    public static Position combine(Position p1, Position p2) {
        if (p1 == null || p2 == null) return null;
        Position p = new Position();
        p.beginLine = p1.beginLine;
        p.beginColumn = p1.beginColumn;
        p.endLine = p2.endLine;
        p.endColumn = p2.endColumn;
        return p;
    }

    public String toString() {
        if (beginLine == endLine) {
            if (beginColumn == endColumn) 
                return  
                    "["+beginLine+","+
                    beginColumn+"]";
            else return 
                "["+beginLine+","+
                beginColumn+"-"+endColumn+"]";
        }
        return 
            "["+beginLine+","+
            beginColumn+"  -  "+
            endLine+","+
            endColumn+"]";
    }
}
    