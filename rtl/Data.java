class Data implements RtlDec {
    private String label;
    private int size;

    public Data (String _label, int _size){
        label =_label;
        size =_size;
    }

    public String getLabel (){
        return label;
    }

    public void setLabel (String _label){
        label =_label;
    }

    public int getSize (){
        return size;
    }

    public void setSize (int _size){
        size =_size;
    }

    public String toString(){
        return "\ndata" + "(" + label + " " + size + ")";
    };
}