class LabDef implements RtlInsn {
    private String label;

    public LabDef (String _label){
	label =_label;
    }

    public String getLabel (){
	return label;
    }

    public void setLabel (String _label){
	label =_label;
    }

    public String toString(){
	return "labdef" + "(" + label + ")";
    };
}
