package rtl;

public class Label {
    private String name;

    public Label(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        return name + ":";
    }
}
