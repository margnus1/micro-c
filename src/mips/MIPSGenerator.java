package mips;

import rtl.BinOp;
import rtl.*;

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

        //push the arguments
        //a0,a1,a2,a3
        for(int i=0; i < Math.min(argNum,4); i++){
            writeRtlRegister(i+1, MipsRegister.getArgRegister(i));
        }

        //start of the body
        for (Object instruction : proc.getInstructions()){
            generateInstruction(instruction);
        }

        //load $ra
        os.emitMemory("lw",MipsRegister.RA, offsetRA, MipsRegister.FP);

        //load return value
        readRtlRegister(0,MipsRegister.V0);

        //adjust the frame pointer
        os.emitMemory("lw",MipsRegister.FP,offsetFP,MipsRegister.SP);

        //pop the stack
        os.emitInstruction("addiu", MipsRegister.SP, MipsRegister.SP, offsetStack);

        //jump back
        os.emitInstruction("jr", MipsRegister.RA);


    }

    private void generateInstruction(Object instruction) {
        if(instruction instanceof ArrayAddress){
            int destRtlReg = ((ArrayAddress) instruction).getDest();
            int arrBaseOffest = ((ArrayAddress) instruction).getOffset();

            os.emitInstruction("addiu", MipsRegister.T0, MipsRegister.FP, arrBaseOffest + sf.getArrayOffset());
            writeRtlRegister(destRtlReg,MipsRegister.T0);

        }else if(instruction instanceof Binary){
            int lhsReg = ((Binary) instruction).getLhs();
            int rhsReg = ((Binary) instruction).getRhs();
            int destReg = ((Binary) instruction).getDest();

            BinOp op = ((Binary) instruction).getOp();

            readRtlRegister(lhsReg, MipsRegister.T0);
            readRtlRegister(rhsReg,MipsRegister.T1);
            generateBinaryInstruction(op,MipsRegister.T0,MipsRegister.T0,MipsRegister.T1);
            writeRtlRegister(destReg,MipsRegister.T0);

        }else if(instruction instanceof Branch){
            int condReg = ((Branch) instruction).getCond();
            BranchMode modeEnum = ((Branch) instruction).getMode();
            String opName = modeEnum == BranchMode.Zero ? "beq":"bne";
            String branchLabel = ((Branch) instruction).getName();

            readRtlRegister(condReg,MipsRegister.T0);
            os.emitInstruction(opName,MipsRegister.ZERO,MipsRegister.T0,branchLabel);


        }else if(instruction instanceof Call){
            int[] argRegArr = ((Call) instruction).getArgs();

            os.emitInstruction("addiu", MipsRegister.SP, MipsRegister.SP, -4 * argRegArr.length);

            for(int i=0; i<argRegArr.length; i++){
                MipsRegister reg = i > 3 ? MipsRegister.T0 : MipsRegister.getArgRegister(i);
                readRtlRegister(argRegArr[i],reg);
                if(i>3){
                    os.emitMemory(getStoreOp(proc.getRegisterTypes().get(argRegArr[i])),
                            reg,4*i,MipsRegister.SP);
                }
            }

            String funcLabel = ((Call) instruction).getProcName();

            os.emitInstruction("jal",funcLabel);

            os.emitInstruction("addiu", MipsRegister.SP, MipsRegister.SP, 4 * argRegArr.length);

            if (((Call) instruction).getDest() != -1)
                writeRtlRegister(((Call) instruction).getDest(),MipsRegister.V0);



        }else if(instruction instanceof GlobalAddress){
            String globalLabel = ((GlobalAddress) instruction).getName();
            int destReg = ((GlobalAddress) instruction).getDest();

            os.emitInstruction("la", MipsRegister.T0, globalLabel);
            writeRtlRegister(destReg,MipsRegister.T0);

        }else if(instruction instanceof IntConst){
           int val = ((IntConst) instruction).getConstVal();
           int destReg = ((IntConst) instruction).getDest();

            os.emitInstruction("li",MipsRegister.T0, val);
            writeRtlRegister(destReg, MipsRegister.T0);

        }else if(instruction instanceof Jump){
            String jumpLabel = ((Jump) instruction).getLabelName();
            os.emitInstruction("j",jumpLabel);

        }else if(instruction instanceof  Label){
            os.emitLabel(((Label) instruction).getName());

        }else if(instruction instanceof Load){
            int addrReg = ((Load) instruction).getAddr();
            int destReg = ((Load) instruction).getDest();

            readRtlRegister(addrReg,MipsRegister.T0);
            os.emitMemory(getLoadOp(((Load) instruction).getType()),
                    MipsRegister.T1, 0, MipsRegister.T0);
            writeRtlRegister(destReg,MipsRegister.T1);

        }else if(instruction instanceof Unary){
            UnOp unOp = ((Unary) instruction).getOp();
            int destReg = ((Unary) instruction).getDest();

            readRtlRegister(((Unary) instruction).getArg(),MipsRegister.T0);

            if(unOp == UnOp.Not){
                os.emitInstruction("sltiu",MipsRegister.T0,MipsRegister.T0,1);
            }else{
                String opName;
                switch (unOp){
                    case Mov: opName = "move"; break;
                    case Neg: opName = "neg"; break;
                    default:
                        throw new RuntimeException("Unsupported Operation");
                }
                os.emitInstruction(opName,MipsRegister.T0,MipsRegister.T0);
            }
            writeRtlRegister(destReg,MipsRegister.T0);

        }else if(instruction instanceof Store){
            int valReg = ((Store) instruction).getVal();
            int addrReg = ((Store) instruction).getAddr();


            readRtlRegister(valReg,MipsRegister.T0);
            readRtlRegister(addrReg,MipsRegister.T1);
            os.emitMemory(getStoreOp(((Store) instruction).getType()),
                    MipsRegister.T0, 0, MipsRegister.T1);

        }else{
            throw new RuntimeException("Unsupported RTL instruction");
        }
    }


    private void generateBinaryInstruction(BinOp op, MipsRegister dest, MipsRegister lhs, MipsRegister rhs) {
        String opName;
        switch (op){
            case ADD:  opName = "addu"; break;
            case DIV:  opName = "divu"; break;
            case MUL:  opName = "multu"; break;
            case SUB:  opName = "subu"; break;
            case EQ:   opName = "seq"; break;
            case NE:   opName = "sne"; break;
            case LT:   opName = "slt"; break;
            case GT:   opName = "sgt"; break;
            case LTEQ: opName = "sgt"; break;
            case GTEQ: opName = "slt"; break;
            default:
                throw  new RuntimeException("Unsupported Operation");
        }

        if(op == BinOp.LTEQ || op == BinOp.GTEQ){
            os.emitInstruction(opName,dest,rhs,lhs);
        }else if(op == BinOp.MUL || op == BinOp.DIV){
            os.emitInstruction(opName,lhs,rhs);
            os.emitInstruction("mflo",dest);

        }else{
            os.emitInstruction(opName,dest,lhs,rhs);
        }


    }

    private void writeRtlRegister(int rtlRegister, MipsRegister mipsRegister) {
        os.emitMemory(getStoreOp(proc.getRegisterTypes().get(rtlRegister)),
                mipsRegister,
                sf.getRtlRegOffset(rtlRegister),
                MipsRegister.FP);
    }

    private void readRtlRegister(int rtlRegister, MipsRegister mipsRegister){
        os.emitMemory(getLoadOp(proc.getRegisterTypes().get(rtlRegister)),
                        mipsRegister,
                        sf.getRtlRegOffset(rtlRegister),
                        MipsRegister.FP);
    }

    private String getStoreOp(RtlType t) {
        switch (t){
            case INT:  return "sw";
            case BYTE: return "sb";
            default:
                throw new RuntimeException("Unsupported type");
        }
    }

    private String getLoadOp(RtlType t){
        switch (t){
            case INT: return "lw";
            case BYTE: return "lb";
            default:
                throw new RuntimeException("Unsupported type");
        }
    }


}
