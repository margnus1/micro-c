package rtl;

/**
 * Created by Magnus on 2014-02-20.
 */
public class ArrayAddress {
    private int dest;
    private int offset;

    public ArrayAddress(int dest, int offset) {
        this.dest = dest;
        this.offset = offset;
    }

    public int getDest() {
        return dest;
    }
    public int getOffset() {
        return offset;
    }

    @Override
    public String toString(){
        return Rtl.regToString(dest) + " <- ArrayAddress " + offset;
    }
}
