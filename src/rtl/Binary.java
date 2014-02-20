package rtl;

class Binary {
    private int dest;
    private BinOp op;
    private int lhs;
    private int rhs;

    public Binary (int dest, BinOp op, int lhs, int rhs){
        this.dest = dest;
        this.op   = op;
        this.lhs  = lhs;
        this.rhs  = rhs;
    }

    public int getDest() {
        return dest;
    }
    public void setDest(int dest) {
        this.dest = dest;
    }

    public BinOp getOp (){
        return op;
    }
    public void setOp (BinOp op){
        this.op = op;
    }

    public int getRhs(){
        return rhs;
    }
    public void setRhs(int rhs){
        this.rhs = rhs;
    }

    public int getLhs(){
        return lhs;
    }
    public void setLhs(int lhs){
        this.lhs = lhs;
    }

    public String toString(){
        return Rtl.regToString(dest) + " <- " + op + " " +
                Rtl.regToString(lhs) + ", " + Rtl.regToString(rhs);
    }
}
