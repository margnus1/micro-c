package mips;

/**
 * Created by Magnus on 2014-03-05.
 */
public enum MipsRegister {
    ZERO("zero", 0),
    V0("v0", 2),
    A0("a0", 4),
    A1("a1", 5),
    A2("a2", 6),
    A3("a3", 7),
    T0("t0", 8),
    T1("t1", 9),
    T2("t2", 10),
    SP("sp", 29),
    FP("fp", 30),
    RA("ra", 31);

    private String name;
    private int number;

    MipsRegister(String name, int number) {
        this.name = name;
        this.number = number;
    }

    @Override public String toString() {
        return "$"+name;
    }
}
