package rtl;

class Rtl {
    public static final int RV = 0;
    public static final int FP = 1;

    public static int sizeOf(RtlType t) {
        switch(t) { 
        case BYTE : return 1;
        case LONG : return 4;
        default : 
            return -1;
        }
    }

    public static String regToString(int reg) {
        if (reg==RV) return "RV";
        if (reg==FP) return "FP";
        return "#"+reg;
    }
}