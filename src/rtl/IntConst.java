package rtl;

class IntConst {
    private int dest;
    private int constVal;

    public IntConst(int dest, int constVal){
        this.dest = dest;
        this.constVal = constVal;
    }

    public int getConstVal(){
        return constVal;
    }
    public void setConstVal(int _val){
        constVal =_val;
    }

    public int getDest() {
        return dest;
    }
    public void setDest(int dest) {
        this.dest = dest;
    }

    public String toString(){
        return Rtl.regToString(dest) + " <- IntConst " + constVal;
    }
}