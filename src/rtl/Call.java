package rtl;

class Call {
    private int dest;
    private String procName;
    private int[] args;

    public Call(String procName, int[] args) {
        this(-1, procName, args);
    }

    public Call (int dest, String procName, int[] arg){
        this.dest = dest;
        this.procName = procName;
        this.args = arg;
    }

    public int getDest(){
        return dest;
    }
    public void setDest(int dest){
        this.dest = dest;
    }

    public String getProcName(){
        return procName;
    }
    public void setProcName(String procName){
        this.procName = procName;
    }

    public int[] getArgs (){
        return args;
    }
    public void setArgs (int[] args){
        this.args = args;
    }

    public String toString(){
        StringBuffer string = new StringBuffer();
        if (dest != -1) string.append(Rtl.regToString(dest)).append(" <- ");
        string.append("Call ").append(procName);
        for (int i = 0; i < args.length; i++) {
            if (i != 0) string.append(", "); else string.append(" ");
            string.append(Rtl.regToString(args[i]));
        }

        return string.toString();
    }
}