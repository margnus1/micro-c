class CJump implements RtlInsn {
    private boolean flag;
    private int temp;
    private String label;

    public CJump (boolean _flag, int _temp, String _label){
        flag =_flag;
        temp =_temp;
        label =_label;
    }

    public boolean getFlag (){
        return flag;
    }

    public void setFlag (boolean _flag){
        flag =_flag;
    }

    public int getTemp (){
        return temp;
    }

    public void setTemp (int _temp){
        temp =_temp;
    }

    public String getLabel (){
        return label;
    }

    public void setLabel (String _label){
        label =_label;
    }

    public String toString(){
        return "cjump" + "(" + flag + " " + Rtl.regToString(temp)
            + " " + label + ")";
    };
}
