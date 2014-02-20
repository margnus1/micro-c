package rtl;

class Load {
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
    public void setType (RtlType type){
        this.type = type;
    }

    public int getAddr (){
        return addr;
    }
    public void setAddr (int addr){
        this.addr = addr;
    }

    public int getDest (){
        return dest;
    }
    public void setDest (int dest){
        this.dest = dest;
    }

    public String toString(){
        return Rtl.regToString(dest)  + " <- Load " + type + " " +
            Rtl.regToString(addr);
    }
}
