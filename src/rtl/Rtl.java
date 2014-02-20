package rtl;

class Rtl {
    public static final int RV = 0;

    public static int sizeOf(RtlType t) {
        switch(t) { 
        case BYTE : return 1;
        case INT: return 4;
        default : 
            return -1;
        }
    }

    public static String regToString(int reg) {
        if (reg==RV) return "RV";
        return "r"+reg;
    }
}