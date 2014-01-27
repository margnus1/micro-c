class Load implements RtlInsn {
    private RtlType type;
    private int addr;
    private int dest;

    public Load (RtlType _type, int _addr, int _dest){
	type =_type;
	addr =_addr;
	dest =_dest;
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

    public int getDest (){
	return dest;
    }

    public void setDest (int _dest){
	dest =_dest;
    }

    public String toString(){
	return "load" + "(" + type + " " + 
	    Rtl.regToString(addr) + " " + Rtl.regToString(dest) + ")";
    };
}
