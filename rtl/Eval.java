class Eval implements RtlInsn {
    private int temp;
    private RtlExp exp;

    public Eval (int _temp, RtlExp _exp){
	temp =_temp;
	exp =_exp;
    }

    public int getTemp (){
	return temp;
    }

    public void setTemp (int _temp){
	temp =_temp;
    }

    public RtlExp getExp (){
	return exp;
    }

    public void setExp (RtlExp _exp){
	exp =_exp;
    }

    public String toString(){
	return "eval" + "(" + Rtl.regToString(temp) + " " + exp + ")";
    };
}
