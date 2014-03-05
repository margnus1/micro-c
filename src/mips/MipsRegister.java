package mips;

/**
 * Created by Magnus on 2014-03-05.
 */
public class MipsRegister {
    public static final MipsRegister ZERO = new MipsRegister(0);
    public static final MipsRegister V0   = new MipsRegister(2);
    public static final MipsRegister A0   = new MipsRegister(4);
    public static final MipsRegister A1   = new MipsRegister(5);
    public static final MipsRegister A2   = new MipsRegister(6);
    public static final MipsRegister A3   = new MipsRegister(7);
    public static final MipsRegister T0   = new MipsRegister(8);
    public static final MipsRegister T1   = new MipsRegister(9);
    public static final MipsRegister T2   = new MipsRegister(10);
    public static final MipsRegister SP   = new MipsRegister(29);
    public static final MipsRegister FP   = new MipsRegister(30);
    public static final MipsRegister RA   = new MipsRegister(31);

    private static final String[] names = {"zero", "at", "v0", "v1", "a0",
                                 /*  5: */   "a1", "a2", "a3", "t0", "t1",
                                 /* 10: */   "t2", "t3", "t4", "t5", "t6",
                                 /* 15: */   "t7", "s0", "s1", "s2", "s3",
                                 /* 20: */   "s4", "s5", "s6", "s7", "t8",
                                 /* 25: */   "t9", "k0", "k1", "gp", "sp",
                                 /* 30: */   "fp", "ra"};
    private int number;

    MipsRegister(int number) {
        if (number < 0 ||number > 31)
            throw new IllegalArgumentException("Argument number out of range");
        this.number = number;
    }

    public static MipsRegister getArgRegister(int i){
        if (i < 0 || i > 3)
            throw new IllegalArgumentException("Argument i out of range");
        return new MipsRegister(A0.number + i);
    }

    @Override public boolean equals(Object other) {
        if (!(other instanceof MipsRegister)) return false;
        return ((MipsRegister) other).number == number;
    }

    @Override public int hashCode() {
        // We reuse the hashCode of Integer, but scramble it so it is less
        // likely to collide with any Integers.
        return (((Integer)number).hashCode() ^ 0x447069db) + 0x73cfaa60;
    }

    @Override public String toString() {
        return "$"+names[number];
    }
}
