package rtl;

/**
 * Created by Magnus on 2014-02-20.
 */
public class Unary {
    private int dest;
    private UnOp op;
    private int arg;

    public Unary(int dest, UnOp op, int arg) {
        this.dest = dest;
        this.op = op;
        this.arg = arg;
    }

    public int getDest() {
        return dest;
    }
    public UnOp getOp() {
        return op;
    }
    public int getArg() {
        return arg;
    }

    @Override
    public String toString() {
        return Rtl.regToString(dest) + " <- " + op + " " + Rtl.regToString(arg);
    }
}
