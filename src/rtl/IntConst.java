package rtl;

public class IntConst {
    private int dest;
    private int constVal;

    public IntConst(int dest, int constVal){
        this.dest = dest;
        this.constVal = constVal;
    }

    public int getConstVal(){
        return constVal;
    }
    public int getDest() {
        return dest;
    }

    public String toString(){
        return Rtl.regToString(dest) + " <- IntConst " + constVal;
    }
}