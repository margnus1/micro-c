package mips;

import rtl.*;
import utils.Alignment;

import java.util.Map.Entry;
import java.util.*;

/**
 * Instances of this class carry the context of a specific procedure proc.
 */
public class MIPSGenerator {
    MipsOutputStream os;
    StackFrame sf;
    Proc proc;

    public MIPSGenerator(MipsOutputStream os, Proc proc){
        this.os = os;
        this.proc = proc;
    }

    public static void generateCode(MipsOutputStream os, Module rtl){
        for (Entry<String, Integer> e : rtl.getGlobals().entrySet()){
            os.emitGlobal(e.getKey(),e.getValue());
        }

        //get the procedures
        for(Proc proc : rtl.getProcedures()){
            MIPSGenerator instance = new MIPSGenerator(os, proc);
            instance.generateProc();
        }
    }

    private enum Spills {RA, OldFP}
    private void generateProc() {
        //output the label of procedure
        os.emitProcedure(proc.getName());

        //calculate the offset of stack
        int argNum = proc.getArgumentCount();

        // Describe the stack frame by pushing (aribtrarily chosen, but
        // necessarily unique) keys (names) of the slots in order of
        // decreasing memory addresses
        sf = new StackFrame(argNum);
        sf.pushArguments();
        sf.setInitialSP();
        sf.pushWordTemporary(Spills.RA);
        sf.pushWordTemporary(Spills.OldFP);
        List<RtlType> registers = proc.getRegisterTypes();
        sf.pushTemporary(0, registers.get(0));
        for(int i = argNum+1; i < registers.size(); i++){
            //rest of the temporaries
            sf.pushTemporary(i,registers.get(i));
        }
        sf.pushArraySpace(proc.getStackFrameSize());
        sf.finalise(4); // We require sp to be word-aligned

        //allocate stack space
        int offsetStack = sf.getOffsetFromInitalSP();
        os.emitInstruction("addiu", MipsRegister.SP, MipsRegister.SP, -offsetStack);

        //push the arguments
        //a0,a1,a2,a3
        for(int i=0; i < Math.min(argNum,4); i++){
            writeRtlRegister(i+1, MipsRegister.getArgRegister(i));
        }

        //store ra
        int offsetRA = sf.getTemporaryOffset(Spills.RA);
        os.emitMemory("sw", MipsRegister.RA, offsetRA, MipsRegister.SP);

        //store old fp
        int offsetFP = sf.getTemporaryOffset(Spills.OldFP);
        os.emitMemory("sw", MipsRegister.FP, offsetFP, MipsRegister.SP);

        //adjust the fp
        //...to be done
        //pseudo-instruction MOVE: move $fp <- $sp
        os.emitInstruction("move", MipsRegister.FP, MipsRegister.SP);

        //start of the body
        for (Object instruction : proc.getInstructions()){
            if(instruction instanceof ArrayAddress){
                int destRtlReg = ((ArrayAddress) instruction).getDest();
                int arrBaseOffest = ((ArrayAddress) instruction).getOffset();

                os.emitInstruction("addiu", MipsRegister.T0, MipsRegister.FP, arrBaseOffest + sf.getArrayOffset());
                os.emitMemory("sw", MipsRegister.T0, sf.getTemporaryOffset(destRtlReg), MipsRegister.FP);

            }else if(instruction instanceof Binary){

            }else if(instruction instanceof Branch){

            }else if(instruction instanceof Call){

            }else if(instruction instanceof GlobalAddress){

            }else if(instruction instanceof IntConst){

            }else if(instruction instanceof Jump){

            }else if(instruction instanceof  Label){

            }else if(instruction instanceof Load){

            }else if(instruction instanceof Unary){

            }else if(instruction instanceof Store){

            }else{
                throw new RuntimeException("Unsupported RTL instruction");
            }
        }
    }

    private void writeRtlRegister(int rtlRegister, MipsRegister mipsRegister) {
        os.emitMemory(getStoreOp(proc.getRegisterTypes().get(rtlRegister)),
                mipsRegister,
                sf.getRtlRegOffset(rtlRegister),
                MipsRegister.SP);
    }

    private String getStoreOp(RtlType t) {
        switch (t){
            case INT:  return "sw";
            case BYTE: return "sb";
            default:
                throw new RuntimeException("Unsupported type");
        }
    }
}
