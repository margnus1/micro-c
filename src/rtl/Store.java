package rtl;

class Store {
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
    public void setType (RtlType type){
        this.type =type;
    }

    public int getAddr (){
        return addr;
    }
    public void setAddr (int addr){
        this.addr =addr;
    }

    public int getVal (){
        return val;
    }
    public void setVal (int val){
        this.val =val;
    }

    public String toString(){
        return Rtl.regToString(addr) + " <- Store " + type + " " +
                Rtl.regToString(val);
    }
}


