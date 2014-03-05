package mips;

import rtl.RtlType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a stack frame of a mips function.
 * Constructed by pushing things in order of decreasing addresses.
 * Offsets change when pushing things on the stack frame. All values must thus
 * be pushed before any offsets are used.
 */
public class StackFrame {
    private int argumentCount;
    private int[] argumentOffsets;
    protected int byteSize = 0;
    private int initialSP = 0;
    private Map<Object, Integer> temporaries = new HashMap<>();

    public StackFrame(int argumentCount) {
        this.argumentCount = argumentCount;
        this.argumentOffsets = new int[argumentCount];
        for (int i = 0; i < argumentCount; i++) argumentOffsets[i] = -1;
    }

    private int arrayOffset = -1;

    /**
     * Call when you reach the location of the initial SP, if not in the very beginning of the program.
     * O32 example:
     *     StackFrame sf = new StackFrame();
     *     sf.pushArguments(2);
     *     sf.setInitialSP();
     *     sf.pushWordTemporary(RtlReg(0));
     *     sf.pushByteTemporary(RtlReg(3));
     */
    public void setInitialSP() {
        initialSP = byteSize;
    }

    public void pushArgument(int index) {
        byteSize = argumentOffsets[index] = utils.Alignment.align(byteSize, 4) + 4;
    }
    public void pushArguments(int count) {
        for (int index = count - 1; index >= 0; index++) {
            pushArgument(index);
        }
    }

    public void pushTemporary(Object key, RtlType type){
        switch (type) {
            case BYTE: pushByteTemporary(key); return;
            case INT:  pushWordTemporary(key); return;
        }
    }
    public void pushWordTemporary(Object key) {
        temporaries.put(key, byteSize =  utils.Alignment.align(byteSize, 4) + 4);
    }
    public void pushByteTemporary(Object key) {
        temporaries.put(key, byteSize);
        byteSize += 1;
    }

    public void pushArraySpace(int size) {
        byteSize = arrayOffset = utils.Alignment.align(byteSize, 4) + size;
    }

    public void finalise(int alignment) {
        byteSize = utils.Alignment.align(byteSize, alignment);
    }

    public int getOffsetFromInitalSP() {
        return byteSize - initialSP;
    }
    public int getTotalSize() {
        return byteSize;
    }
    public int getArgumentOffset(int index) {
        return invertOffset(argumentOffsets[index]);
    }
    public int getArrayOffset() {
        return invertOffset(arrayOffset);
    }
    public int getTemporaryOffset(Object key) {
        return invertOffset(temporaries.get(key));
    }

    private int invertOffset(int offsetFromHighestAddress) {
        if (offsetFromHighestAddress == -1) throw new RuntimeException("Invalid offset");
        return byteSize - offsetFromHighestAddress;
    }
}
