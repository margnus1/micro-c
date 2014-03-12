package mips;

/**
 * Created by Magnus on 2014-03-05.
 */
public abstract class MipsOutputStream {
    public abstract void emitProcedure(String label);
    public abstract void emitInstruction(String instruction, String... arguments);
    public abstract void emitLabel(String label);
    public void emitComment(String comment) {}

    /* Convenience wrappers around emitInstruction */
    public void emitMemory(String instruction, MipsRegister data, int offset, MipsRegister addrReg) {
        emitInstruction(instruction, data.toString(), offset + "(" + addrReg + ")");
    }
    public void emitMemory(String instruction, MipsRegister data, String label, MipsRegister addrReg) {
        if (addrReg.equals(MipsRegister.ZERO))
            emitInstruction(instruction, data.toString(), label);
        else
            emitInstruction(instruction, data.toString(), label + "(" + addrReg + ")");
    }
    public void emitInstruction(Object instruction, Object... arguments) {
        String[] argStrings = new String[arguments.length];
        for (int i = 0; i < arguments.length; i++)
            argStrings[i] = arguments[i].toString();
        emitInstruction(instruction.toString(), argStrings);
    }

    @Deprecated public void emitRType(MipsROp op, MipsRegister rs, MipsRegister rt, MipsRegister rd) {
        emitInstruction(op.toString().toLowerCase(), rs.toString(), rt.toString(), rd.toString());
    }
    @Deprecated public void emitIType(MipsIOp op, MipsRegister rs, MipsRegister rt, int immediate) {
        emitInstruction(op.toString().toLowerCase(), rs.toString(), rt.toString(), Integer.toString(immediate));
    }
    @Deprecated public void emitIType(MipsIOp op, MipsRegister rs, MipsRegister rt, String label) {
        emitInstruction(op.toString().toLowerCase(), rs.toString(), rt.toString(), label);
    }
    @Deprecated public void emitJType(MipsJOp op, String label) {
        emitInstruction(op.toString().toLowerCase(), label);
    }

    public abstract void emitGlobal(String label, int size);
    public abstract void emitGlobal(String label, String stringLiteral);
}
