package rtl;

public class Store {
    private int addr;
    private RtlType type;
    private int val;

    public Store (int addr, RtlType type, int val){
        this.type = type;
        this.addr = addr;
        this.val  = val;
    }

    public RtlType getType (){
        return type;
    }
    public int getAddr (){
        return addr;
    }
    public int getVal (){
        return val;
    }

    public String toString(){
        return Rtl.regToString(addr) + " <- Store " + type + " " +
                Rtl.regToString(val);
    }
}


