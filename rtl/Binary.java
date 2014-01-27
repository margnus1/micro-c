class Binary implements RtlExp {
    private RtlBinop op;
    private int right;
    private int left;

    public Binary (RtlBinop _op, int _right, int _left){
	op =_op;
	right =_right;
	left =_left;
    }

    public RtlBinop getOp (){
	return op;
    }

    public void setOp (RtlBinop _op){
	op =_op;
    }

    public int getRight (){
	return right;
    }

    public void setRight (int _right){
	right =_right;
    }

    public int getLeft (){
	return left;
    }

    public void setLeft (int _left){
	left =_left;
    }

    public String toString(){
	return "binary" + "(" + op + " " + Rtl.regToString(right)
	    + " " + Rtl.regToString(left) + ")";
    };
}
