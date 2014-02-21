package rtl;

/**
 * Created by Magnus on 2014-02-21.
 */
public class GlobalAddress {
    private int dest;
    private String name;

    public GlobalAddress(int dest, String name) {
        this.dest = dest;
        this.name = name;
    }

    public int getDest() {
        return dest;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return Rtl.regToString(dest) + " <- GlobalAddress " + name;
    }
}
