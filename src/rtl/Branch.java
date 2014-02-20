package rtl;

class Branch {
    private String name;
    private BranchMode mode;
    private int cond;

    public Branch(String name, BranchMode mode, int cond){
        this.cond = cond;
        this.mode = mode;
        this.name = name;
    }

    public BranchMode getMode(){
        return mode;
    }
    public void setMode(BranchMode mode){
        this.mode = mode;
    }

    public int getCond(){
        return cond;
    }
    public void setCond(int cond){
        this.cond = cond;
    }

    public String getName(){
        return name;
    }
    public void setName(String _label){
        name = _label;
    }

    public String toString(){
        return "Branch " + name + ", " + mode + ", " + Rtl.regToString(cond);
    }
}
