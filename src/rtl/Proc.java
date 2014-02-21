package rtl;
import java.util.*;

public class Proc {
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
        this.registerTypes = Collections.unmodifiableList(registerTypes);
        this.stackFrameSize = stackFrameSize;
        this.instructions = Collections.unmodifiableList(instructions);
    }

    public String getName(){
        return name;
    }
    public int getArgumentCount() {
        return argumentCount;
    }
    public List<RtlType> getRegisterTypes() {
        return registerTypes;
    }
    public int getStackFrameSize(){
        return stackFrameSize;
    }
    public List<Object> getInstructions(){
        return instructions;
    }

    public String toString(){
        StringBuilder r = new StringBuilder("Procedure " + name + "\n");
        r.append("  Argument count:" + argumentCount + "\n");
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
