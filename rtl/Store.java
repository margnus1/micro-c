package rtl;

class Store implements RtlInsn {
    private RtlType type;
    private int addr;
    private int val;

    public Store (RtlType _type, int _addr, int _val){
        type =_type;
        addr =_addr;
        val =_val;
    }

    public RtlType getType (){
        return type;
    }

    public void setType (RtlType _type){
        type =_type;
    }

    public int getAddr (){
        return addr;
    }

    public void setAddr (int _addr){
        addr =_addr;
    }

    public int getVal (){
        return val;
    }

    public void setVal (int _val){
        val =_val;
    }

    public String toString(){
        return "store" + "(" + type + " " + 
            Rtl.regToString(addr) + " " + Rtl.regToString(val) + ")";
    };
}


