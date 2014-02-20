package rtl;

class Jump {
    private String labelName;

    public Jump (String labelName){
        this.labelName = labelName;
    }

    public String getLabelName(){
        return labelName;
    }
    public void setLabelName(String labelName){
        this.labelName = labelName;
    }

    public String toString(){
        return "Jump" + " " + labelName;
    }
}
