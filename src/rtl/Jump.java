package rtl;

public class Jump {
    private String labelName;

    public Jump (String labelName){
        this.labelName = labelName;
    }

    public String getLabelName(){
        return labelName;
    }

    public String toString(){
        return "Jump" + " " + labelName;
    }
}
