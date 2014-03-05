package mips;

/**
 * Created by Magnus on 2014-03-05.
 */
public abstract class MipsOutputStream {
    public abstract void emitProcedure(String label);
    public abstract void emitRType(MipsROp op, MipsRegister rs, MipsRegister rt, MipsRegister rd);
    public abstract void emitIType(MipsIOp op, MipsRegister rs, MipsRegister rt, int immediate);
    public abstract void emitIType(MipsIOp op, MipsRegister rs, MipsRegister rt, String label);
    public abstract void emitJType(MipsJOp op, String label);
    public abstract void emitLabel(String label);

    public abstract void emitGlobal(String label, int size);
}
