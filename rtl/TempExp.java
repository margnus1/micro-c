class TempExp implements RtlExp {
    private int temp;

    public TempExp (int _temp){
        temp =_temp;
    }

    public int getTemp (){
        return temp;
    }

    public void setTemp (int _temp){
        temp =_temp;
    }

    public String toString(){
        return "tempexp" + "(" + Rtl.regToString(temp) + ")";
    };
}
