package rtl;
import java.util.*;

class Proc {
    private String name;
    private int argumentCount;
    private List<RtlType> registerTypes;
    private int stackFrameSize;
    private List<Object> instructions;

    public Proc (String name, int argumentCount,
                 List<RtlType> registerTypes, int stackFrameSize,
                 List<Object> instructions){
        this.name = name;
        this.argumentCount = argumentCount;
        this.registerTypes = registerTypes;
        this.stackFrameSize = stackFrameSize;
        this.instructions = instructions;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public int getArgumentCount() {
        return argumentCount;
    }
    public void setArgumentCount(int argumentCount) {
        this.argumentCount = argumentCount;
    }

    public List<RtlType> getRegisterTypes() {
        return registerTypes;
    }
    public void setRegisterTypes(List<RtlType> registerTypes) {
        this.registerTypes = registerTypes;
    }

    public int getStackFrameSize(){
        return stackFrameSize;
    }
    public void setStackFrameSize(int stackFrameSize){
        this.stackFrameSize = stackFrameSize;
    }

    public List<Object> getInstructions(){
        return instructions;
    }
    public void setInstructions(List<Object> instructions){
        this.instructions = instructions;
    }

    public String toString(){
        StringBuilder r = new StringBuilder("Procedure " + name + "\n");
        r.append("  Argument count:" + argumentCount + "\n")
        r.append("  Stack frame size:" + stackFrameSize + "\n");

        r.append("  Register types:\n");
        for (int i = 0; i < registerTypes.size(); i++) {
            r.append("    " + Rtl.regToString(i) + ": " + registerTypes.get(i) + "\n");
        }

        r.append("  Instructions:");

        for (Object ins : instructions) {
            if (ins instanceof Label) r.append("\n  "+ins);
            else r.append("\n    "+ins);
        }
        return r.toString();
    }
}

