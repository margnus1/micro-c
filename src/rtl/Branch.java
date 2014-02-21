package rtl;

public class Branch {
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
    public int getCond(){
        return cond;
    }
    public String getName(){
        return name;
    }

    public String toString(){
        return "Branch " + name + ", " + mode + ", " + Rtl.regToString(cond);
    }
}
