package rtl;

class Label {
    private String name;

    public Label(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String toString(){
        return name + ":";
    }
}
