class Jump implements RtlInsn {
    private String label;

    public Jump (String _label){
        label =_label;
    }

    public String getLabel (){
        return label;
    }

    public void setLabel (String _label){
        label =_label;
    }

    public String toString(){
        return "jump" + "(" + label + ")";
    };
}
