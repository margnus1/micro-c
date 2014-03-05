package mips;

import rtl.*;
import utils.Alignment;

import java.util.Map.Entry;
import java.util.*;




/**
 * Created by Doris on 14-3-5.
 */
public class MIPSGenerator {
    MipsOutputStream os;

    public MIPSGenerator(MipsOutputStream os){
        this.os = os;
    }

    public  void generateCode(Module rtl){
        if(!rtl.getGlobals().isEmpty()){
            //...output.
            for (Entry<String, Integer> e : rtl.getGlobals().entrySet()){
                os.emitGlobal(e.getKey(),e.getValue());
            }
        }

        //get the procedures
        for(Proc proc : rtl.getProcedures()){
            generateProc(proc);

        }
    }

    private enum Spills {RA, OldFP}
    private void generateProc(Proc proc) {
        //output the label of procedure
        os.emitProcedure(proc.getName());

        //calculate the offset of stack
        int argNum = proc.getArgumentCount();

        StackFrame sf = new StackFrame(argNum);
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

        int offsetStack = sf.getOffsetFromInitalSP();

        //allocate stack space
        os.emitIType(MipsIOp.ADDIU,MipsRegister.SP,MipsRegister.SP,-offsetStack);

        //push the arguments
        //a0,a1,a2,a3

        for(int i=0; i < Math.min(argNum,4); i++){
            MipsIOp storeOp;
            switch (registers.get(i+1)){
                case INT:
                    storeOp = MipsIOp.SW;
                    break;
                case BYTE:
                    storeOp = MipsIOp.SB;
                    break;
                default:
                    throw new RuntimeException("Unsupported type");
            }

            os.emitIType(storeOp,
                    MipsRegister.getArgRegister(i),
                    MipsRegister.SP,
                    sf.getArgumentOffset(i));
        }

        //store ra
        int offsetRA = sf.getTemporaryOffset(Spills.RA);
        os.emitIType(MipsIOp.SW, MipsRegister.RA, MipsRegister.SP, offsetRA);

        //store old fp
        int offsetFP = sf.getTemporaryOffset(Spills.OldFP);
        os.emitIType(MipsIOp.SW,MipsRegister.FP,MipsRegister.SP,offsetFP);

        //adjust the fp
        //...to be done
        //pseudo-instruction MOVE: move $fp <- $sp
        os.emitIType(MipsIOp.ADDIU,MipsRegister.FP,MipsRegister.SP,0);

        //start of the body
        for (Object instruction : proc.getInstructions()){
            if(instruction instanceof ArrayAddress){
                int destRtlReg = ((ArrayAddress) instruction).getDest();

                int arrBaseOffest = ((ArrayAddress) instruction).getOffset();

                //addiu($t0,$fp,offset)
                os.emitIType(MipsIOp.ADDIU, MipsRegister.T0, MipsRegister.FP, arrBaseOffest + sf.getArrayOffset());

                //sw($t0,...(sp))
                os.emitIType(MipsIOp.SW, MipsRegister.T0, MipsRegister.FP, sf.getTemporaryOffset(destRtlReg));


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
}
