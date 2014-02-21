package rtl;

public class Load {
    private int dest;
    private RtlType type;
    private int addr;

    public Load (int dest, RtlType type, int addr){
        this.dest = dest;
        this.type = type;
        this.addr = addr;
    }

    public RtlType getType (){
        return type;
    }
    public int getAddr (){
        return addr;
    }
    public int getDest (){
        return dest;
    }

    public String toString(){
        return Rtl.regToString(dest)  + " <- Load " + type + " " +
            Rtl.regToString(addr);
    }
}
