class Icon implements RtlExp {
    private int val;

    public Icon (int _val){
	val =_val;
    }

    public int getVal (){
	return val;
    }

    public void setVal (int _val){
	val =_val;
    }

    public String toString(){
	return "icon" + "(" + val + ")";
    };
}
