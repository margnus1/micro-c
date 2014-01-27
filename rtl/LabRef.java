package rtl;

class LabRef implements RtlExp {
    private String label;

    public LabRef (String _label){
        label =_label;
    }

    public String getLabel (){
        return label;
    }

    public void setLabel (String _label){
        label =_label;
    }

    public String toString(){
        return "labref" + "(" + label + ")";
    };
}
